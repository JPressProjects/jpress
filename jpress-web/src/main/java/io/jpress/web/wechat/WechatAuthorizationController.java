/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.wechat;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HttpKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.SessionUtils;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;
import io.jpress.web.base.ControllerBase;

import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.wechat
 */
@RequestMapping("/wechat/authorization")
public class WechatAuthorizationController extends ControllerBase {


    /**
     * 获取用户信息的url地址
     * 会弹出框让用户进行授权
     */
    public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize"
            + "?appid={appid}"
            + "&redirect_uri={redirecturi}"
            + "&response_type=code"
            + "&scope=snsapi_userinfo"
            + "&state=235#wechat_redirect";


    /**
     * 静默授权的url地址
     */
    public static final String BASE_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize"
            + "?appid={appid}"
            + "&redirect_uri={redirecturi}"
            + "&response_type=code"
            + "&scope=snsapi_base"
            + "&state=235#wechat_redirect";


    @Inject
    private UserService userService;


    public void index() {

        String gotoUrl = getPara("goto");


        String appId = JPressOptions.get(JPressConsts.OPTION_WECHAT_APPID);
        if (StrUtil.isBlank(appId)) {
            renderText("管理员的微信APPID配置错误，请联系管理在后台 -> 微信 -> 基础设置 配置正确的APPID。");
            return;
        }


        String domain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        if (StrUtil.isBlank(domain)) {
            domain = RequestUtil.getBaseUrl(getRequest());
        }


        //这个backUrl是微信执行完用户授权之后跳转回来的url
        String backUrl = domain + "/wechat/authorization/back?goto=" + gotoUrl;

        String wechatUrl = AUTHORIZE_URL.replace("{appid}", appId)
                .replace("{redirecturi}", backUrl);

        redirect(wechatUrl);
    }


    public void back() {
        String gotoUrl = getPara("goto");
        String code = getPara("code");

        String appId = JPressOptions.get(JPressConsts.OPTION_WECHAT_APPID);
        String appSecret = JPressOptions.get(JPressConsts.OPTION_WECHAT_APPSECRET);

        if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
            renderText("管理员的微信AppId或AppSecret配置错误，请联系管理在后台 -> 微信 -> 基础设置 配置正确。");
            return;
        }

        ApiResult result = getOpenId(appId, appSecret, code);
        if (result == null) {
            renderText("网络错误，获取不到微信信息，请联系管理员");
            return;
        }

        //在某些时候获取不到微信信息
        //一般情况下是code有问题
        //重复发起刚才的过程
        if (result.isSucceed() == false) {
            redirect(StrUtil.urlDecode(gotoUrl));
            return;
        }

        String openId = result.getStr("openid");
        String accessToken = result.getStr("access_token");

        ApiResult userInfoResult = getUserInfo(openId, accessToken);

        Long userId = doGetOrCreateUser(userInfoResult);
        if (userId == null) {
            //这种情况非常严重，一般情况下只有链接不上数据库了
            //或者是在 RPC 下，无法调用到 provider 了
            renderText("can not query user or save user to database");
            return;
        }

        SessionUtils.record(userId);
        CookieUtil.put(this, JPressConsts.COOKIE_UID, userId);
        redirect(StrUtil.urlDecode(gotoUrl));
    }


    private static ApiResult getUserInfo(String openId, String accessToken) {
        ParaMap pm = ParaMap.create("access_token", accessToken).put("openid", openId).put("lang", "zh_CN");
        return new ApiResult(HttpKit.get("https://api.weixin.qq.com/sns/userinfo", pm.getData()));
    }


    private static ApiResult getOpenId(String appId, String appSecret, String code) {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid={appid}"
                + "&secret={secret}" + "&code={code}" + "&grant_type=authorization_code";

        String getOpenIdUrl = url.replace("{appid}", appId)
                .replace("{secret}", appSecret)
                .replace("{code}", code);

        String jsonResult = null;
        try {
            jsonResult = HttpUtils.get(getOpenIdUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonResult == null) {
            return null;
        }

        return new ApiResult(jsonResult);
    }

    public Long doGetOrCreateUser(ApiResult apiResult) {

        /**
         * {
         "subscribe": 1,
         "openid": "xxx",
         "nickname": "Band",
         "sex": 1,
         "language": "zh_CN",
         "city": "广州",
         "province": "广东",
         "country": "中国",
         "headimgurl":  "xxx",
         "subscribe_time": xxxx,
         "unionid": " xxxx"
         "remark": "",
         "groupid": 0,
         "tagid_list":[128,2]
         }
         */

        String openId = apiResult.get("openid");
        String unionId = apiResult.get("unionid");


        User user = null;

        //优先根据 unioinId 进行查询
        if (StrUtil.isNotBlank(unionId)) {
            user = userService.findFirstByWxUnionid(unionId);
            if (user != null) {
                return user.getId();
            }
        }

        //之后根据 openId 进行查询
        if (StrUtil.isNotBlank(openId)) {
            user = userService.findFirstByWxOpenid(openId);
            if (user != null) {
                return user.getId();
            }
        }

        UserOpenidService userOpenidService = Aop.get(UserOpenidService.class);

        String uid = CookieUtil.get(this, JPressConsts.COOKIE_UID);
        if (StrUtil.isNotBlank(uid)) {
            user = userService.findById(uid);
            if (user != null) {
                userOpenidService.saveOrUpdate(user.getId(), UserOpenid.TYPE_WECHAT, openId);
                if (StrUtil.isNotBlank(unionId)) {
                    userOpenidService.saveOrUpdate(user.getId(), UserOpenid.TYPE_WECHAT_UNIONID, unionId);
                }

                return user.getId();
            }
        }


        // 都查询不到，说明该用户是一个新的用户，创建一个新的用户
        String nickName = apiResult.get("nickname");
        int sex = apiResult.get("sex");
        String city = apiResult.get("city");
        String province = apiResult.get("province");
        String country = apiResult.get("country");
        String avatarUrl = apiResult.get("headimgurl");

        user = new User();
        user.setNickname(nickName);
        user.setAddress(country + province + city);
//        user.setWxUnionid(unionId);
//        user.setWxOpenid(openId);
        user.setAvatar(avatarUrl);
        user.setCreated(new Date());
        user.setLogged(new Date());
        user.setCreateSource(User.SOURCE_WECHAT_WEB);
        user.setAnonym(CookieUtil.get(this, JPressConsts.COOKIE_ANONYM));

        boolean isNotActivate = JPressOptions.getAsBool("reg_users_is_not_activate");
        if (isNotActivate) {
            user.setStatus(User.STATUS_REG);
        }else {
            user.setStatus(User.STATUS_OK);
        }


        Long userId = (Long) userService.save(user);


        userOpenidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT, openId);
        if (StrUtil.isNotBlank(unionId)) {
            userOpenidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT_UNIONID, unionId);
        }


        return userId;
    }

}
