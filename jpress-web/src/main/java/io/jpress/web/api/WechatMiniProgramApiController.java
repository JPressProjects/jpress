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
package io.jpress.web.api;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiResult;
import io.jboot.Jboot;
import io.jboot.apidoc.annotation.Api;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;
import io.jpress.web.api.html2wxml.Html2WxmlController;
import io.jpress.web.interceptor.ApiInterceptor;

import java.util.Date;

/**
 * 小程序相关的API
 */
@RequestMapping("/api/wechat/mp")
@Before({ApiInterceptor.class,
        WechatMiniProgramApiInterceptor.class})
@Api(value = "微信小程序相关API",collect = Html2WxmlController.class)
public class WechatMiniProgramApiController extends BaseWechatDecryptController {

    @Inject
    private UserService userService;


    @Override
    protected void onLoginSuccess(ApiResult apiResult) {
        String openId = apiResult.get("openid");
        if (Jboot.isDevMode()) {
            System.out.println("current agent openid ---->>>>: " + openId);
        }

        setJwtAttr(JPressConsts.JWT_OPENID, openId);

        String unionId = apiResult.get("unionid");

        //一般情况下，unionId 没有数据，需要通过微信第三方平台绑定才会有这个值
        if (StrUtil.isNotBlank(unionId)) {
            setJwtAttr(JPressConsts.JWT_UNIONID, unionId);
        }

        Ret ret = Ret.ok();

        User user = null;
        if (StrUtil.isNotBlank(unionId)) {
            user = userService.findFirstByWxUnionid(unionId);
        }

        if (user == null) {
            user = userService.findFirstByWxOpenid(openId);
        }

        if (user != null) {
            setJwtAttr(JPressConsts.JWT_USERID, user.getId());
            ret.set("userId", user.getId());
        }

        renderJson(ret);
    }


    @Override
    protected void onGetUserInfo(ApiResult apiResult) {

        String openId = getCurrentWechatOpenId();
        String unionId = getCurrentWechatUnionId();


        String nickName = apiResult.get("nickName");
//        int gender = apiResult.getInt("gender");
        String city = apiResult.get("city");
        String province = apiResult.get("province");
        String country = apiResult.get("country");
        String avatarUrl = apiResult.get("avatarUrl");

        User user = new User();
        user.setNickname(nickName);
        user.setAddress(country + province + city);
        user.setAvatar(avatarUrl);
        user.setCreated(new Date());
        user.setLogged(new Date());
        user.setCreateSource(User.SOURCE_WECHAT_MINIPROGRAM);

        boolean isNotActivate = JPressOptions.getAsBool("reg_users_is_not_activate");
        if (isNotActivate) {
            user.setStatus(User.STATUS_REG);
        } else {
            user.setStatus(User.STATUS_OK);
        }

        Long userId = (Long) userService.save(user);

        UserOpenidService openidService = Aop.get(UserOpenidService.class);
        openidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT, openId);
        openidService.saveOrUpdate(userId, UserOpenid.TYPE_WECHAT_UNIONID, unionId);

        setJwtAttr(JPressConsts.JWT_USERID, user.getId());
        setJwtAttr(JPressConsts.JWT_OPENID, openId);

        //一般情况下，unionId 没有数据，需要通过微信第三方平台绑定才会有这个值
        if (StrUtil.isBlank(unionId)) {
            setJwtAttr(JPressConsts.JWT_UNIONID, unionId);
        }

        renderJson(Ret.ok().set("userId",userId));
    }

    @Override
    protected void onGetPhoneNumber(ApiResult apiResult) {

        String unionId = getCurrentWechatUnionId();
        String openId = getCurrentWechatOpenId();

        User user = null;

        if (StrUtil.isNotBlank(unionId)) {
            user = userService.findFirstByWxUnionid(unionId);
        }

        if (user == null) {
            user = userService.findFirstByWxOpenid(openId);
        }

        if (user != null) {
            user.setMobile(apiResult.get("phoneNumber"));
            user.setMobileStatus(User.STATUS_OK);
            userService.update(user);
            renderOkJson();
        } else {
            renderFailJson("无法获取用户信息");
        }
    }


}
