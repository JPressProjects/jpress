/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.wxaapp.api.WxaUserApi;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;
import io.jpress.web.interceptor.ApiInterceptor;

import java.util.Date;

/**
 * 小程序相关的API
 */
@RequestMapping("/api/wechat/mp")
@Before({ApiInterceptor.class,
        WechatMiniProgramApiInterceptor.class})
public class WechatMiniProgramApiController extends ApiControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private WxaUserApi wxaUserApi;


    /**
     * 1、小程序端调用 wx.login() 之后，会得到code ，
     * 详情：https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
     * <p>
     * 2、小程序端得到code之后，需调用此接口那code来换session_key，同时服务器存储session_key
     * 3、为什么要存储session_key呢？目的是为了获取用户信息
     * 4、小程序调用 wx.login() 之后，接下来会调用 wx.getUserInfo() ，
     * 此时要注意：wx.getUserInfo()得到的信息是加密的，需要解密才能获取文本信息
     * <p>
     * 5、解密就用到刚才的 session_key 了，session_key 其实就是解密的钥匙 （密钥）
     */
    public void code2session() {

        String code = getPara("code");
        if (StrUtil.isBlank(code)) {
            renderFailJson(105, "code is blank");
            return;
        }


        // 获取SessionKey 和 openId
        // 返回{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","expires_in":2592000,"openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
        ApiResult apiResult = wxaUserApi.getSessionKey(code);
        if (!apiResult.isSucceed()) {
            renderFailJson(apiResult.getErrorCode(), apiResult.getErrorMsg());
            return;
        }


        String sessionKey = apiResult.getStr("session_key");
        String sessionId = StrUtil.uuid();

        //把sessionKey存储起来，接下来用户解密要用到这个sessionKey
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        accessTokenCache.set("wxa:session:" + sessionId, sessionKey);


        //把sessionId传给客户端，客户端解密数据的时候，必须把这个sessionId传入进来，才能获取sessionKey
        renderJson(Ret.ok().set("sessionId", sessionId));

    }

    /**
     * 小程序端调用 wx.getUserInfo() 后，得到的是加密的用户数据
     * 需要调用此接口，才能获取到具体的数据
     * 解密用户数据，小程序的相关接口 https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
     */
    public void decryptUserInfo() {


        String postData = getRawData();
        if (StrUtil.isBlank(postData)) {
            renderFailJson(107, "can not get data");
            return;
        }

        JSONObject json = JSON.parseObject(postData);

        //小程序端调用 /api/wechat/mp/code2session之后得到的sessionId
        String sessionId = json.getString("sessionId");

        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionKey = accessTokenCache.get("wxa:session:" + sessionId);
        if (StrUtil.isBlank(sessionKey)) {
            renderFailJson(107, "session id is error.");
            return;
        }


        //不包括敏感信息的原始数据字符串，用于计算签名
        String rawData = json.getString("rawData");

        //签名：使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息
        String signature = json.getString("signature");

        //包括敏感数据在内的完整用户信息的加密数据
        //具体加密方法在：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
        String encryptedData = json.getString("encryptedData");

        //加密算法的初始向量
        String iv = json.getString("iv");


        // 用户信息校验
        boolean check = wxaUserApi.checkUserInfo(sessionKey, rawData, signature);
        if (check == false) {
            renderFailJson(500, "userInfo check fail");
            return;
        }

        // 服务端解密用户信息，得到原始的用户信息
        ApiResult apiResult = wxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        if (!apiResult.isSucceed()) {
            renderFailJson(apiResult.getErrorCode(), apiResult.getErrorMsg());
            return;
        }

        Long userId = doGetOrCreateUser(apiResult);
        if (userId == null) {
            //这种情况非常严重，一般情况下只有链接不上数据库了
            //或者是在 RPC 下，无法调用到 provider 了
            renderFailJson(501, "can not query user or save user to database");
            return;
        }


        setJwtAttr(JPressConsts.JWT_USERID, userId);

        //设置 jwt Token 给客户端
        //以后客户端通过此token定位用户信息
        renderJson(Ret.ok().set("token", createJwtToken()));
    }

    public Long doGetOrCreateUser(ApiResult apiResult) {

        /**
         * 文档：https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
         * apiResult的数据格式如下
         * {
         "openId": "OPENID",
         "nickName": "NICKNAME",
         "gender": GENDER,
         "city": "CITY",
         "province": "PROVINCE",
         "country": "COUNTRY",
         "avatarUrl": "AVATARURL",
         "unionId": "UNIONID",
         "watermark": {
         "appid": "APPID",
         "timestamp": TIMESTAMP
         }
         }
         */

        String openId = apiResult.get("openId");
        String unionId = apiResult.get("unionId");


        User user = null;

        //优先根据 unioinId 进行查询
        if (StrUtil.isNotBlank(unionId)) {
            user = userService.findFistByWxUnionid(unionId);
            if (user != null) {
                return user.getId();
            }
        }

        //之后根据 openId 进行查询
        if (StrUtil.isNotBlank(openId)) {
            user = userService.findFistByWxOpenid(openId);
            if (user != null) {
                return user.getId();
            }
        }

        // 都查询不到，说明该用户是一个新的用户，创建一个新的用户
        String nickName = apiResult.get("nickName");
        int gender = apiResult.getInt("gender");
        String city = apiResult.get("city");
        String province = apiResult.get("province");
        String country = apiResult.get("country");
        String avatarUrl = apiResult.get("avatarUrl");

        user = new User();
        user.setNickname(nickName);
        user.setAddress(country + province + city);
//        user.setWxUnionid(unionId);
//        user.setWxOpenid(openId);
        user.setAvatar(avatarUrl);
        user.setCreated(new Date());
        user.setLogged(new Date());
        user.setCreateSource(User.SOURCE_WECHAT_MINIPROGRAM);

        boolean isNotActivate = JPressOptions.getAsBool("reg_users_is_not_activate");
        if (isNotActivate) {
            user.setStatus(User.STATUS_REG);
        }else {
            user.setStatus(User.STATUS_OK);
        }


        Long userId = (Long) userService.save(user);

        UserOpenidService openidService = Aop.get(UserOpenidService.class);
        openidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT,openId);
        openidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT_UNIONID,unionId);


        return userId;
    }

}
