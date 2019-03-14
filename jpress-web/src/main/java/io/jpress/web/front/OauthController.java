/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import io.jboot.utils.CookieUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.oauth2.Oauth2Controller;
import io.jpress.commons.oauth2.OauthConnector;
import io.jpress.commons.oauth2.OauthUser;
import io.jpress.commons.oauth2.connector.QQConnector;
import io.jpress.commons.oauth2.connector.WechatConnector;
import io.jpress.model.User;
import io.jpress.service.UserService;

import java.util.Date;

/**
 * 备注：
 * <p>
 * 当进行qq授权的时候，应该是 /oauth/qq
 * 当进行微信授权的时候，应该是 /oauth/wechat
 * <p>
 * 这个行为由：Oauth2Controller 实现
 */

@RequestMapping("/oauth")
public class OauthController extends Oauth2Controller {

    @Inject
    private UserService userService;


    /**
     * 用户授权成功
     *
     * @param ouser
     */
    @Override
    public void onAuthorizeSuccess(OauthUser ouser) {
        User dbUser = null;
        switch (ouser.getSource()) {
            case "qq":
                dbUser = userService.findFistByQQOpenid(ouser.getOpenId());
                break;
            case "wechat":
                dbUser = userService.findFistByWxOpenid(ouser.getOpenId());
                break;
            default:
                redirect("/user/login");
                return;
        }

        if (dbUser == null) {
            dbUser = new User();
            dbUser.setAvatar(ouser.getAvatar());
            dbUser.setNickname(ouser.getNickname());
            dbUser.setCreateSource(ouser.getSource());
            dbUser.setCreated(new Date());
            dbUser.setGender(ouser.getGender());
            dbUser.setSalt(HashKit.generateSaltForSha256());

            if ("qq".equals(ouser.getSource())) {
                dbUser.setQqOpenid(ouser.getOpenId());
            } else {
                dbUser.setWxOpenid(ouser.getOpenId());
            }

            userService.save(dbUser);
        }

        CookieUtil.put(this, JPressConsts.COOKIE_UID, dbUser.getId());

        //跳转到用户中心
        redirect("/user");
    }


    /**
     * 用户授权失败
     *
     * @param errorMessage
     */
    @Override
    public void onAuthorizeError(String errorMessage) {
        renderText("授权失败，错误信息 ： " + errorMessage);
    }


    /**
     * 通过 参数 para 获取对应的 连接器 oauth connector
     *
     * @param para
     * @return
     */
    @Override
    public OauthConnector onConnectorGet(String para) {
        switch (para) {
            case "qq":
                return createQQConnector();
            case "wechat":
                return createWechatConnector();
        }

        return null;
    }


    private OauthConnector createQQConnector() {
        boolean enable = JPressOptions.getAsBool("login_qq_enable");
        if (enable == false) return null;

        String appkey = JPressOptions.get("login_qq_appkey");
        String appsecret = JPressOptions.get("login_qq_appsecret");
        return new QQConnector("qq", appkey, appsecret);
    }

    private OauthConnector createWechatConnector() {
        boolean enable = JPressOptions.getAsBool("login_wechat_enable");
        if (enable == false) return null;

        String appkey = JPressOptions.get("login_wechat_appkey");
        String appsecret = JPressOptions.get("login_wechat_appsecret");
        return new WechatConnector("wechat", appkey, appsecret);
    }


}
