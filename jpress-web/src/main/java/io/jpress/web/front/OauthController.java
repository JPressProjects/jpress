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
package io.jpress.web.front;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import io.jboot.utils.CookieUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.oauth2.Oauth2Controller;
import io.jpress.commons.oauth2.OauthConnector;
import io.jpress.commons.oauth2.OauthUser;
import io.jpress.commons.oauth2.connector.*;
import io.jpress.model.User;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;
import io.jpress.web.interceptor.UserInterceptor;

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
@Before(UserInterceptor.class)
public class OauthController extends Oauth2Controller {

    @Inject
    private UserService userService;

    @Inject
    private UserOpenidService openidService;


    /**
     * 用户授权成功
     *
     * @param ouser
     */
    @Override
    public void onAuthorizeSuccess(OauthUser ouser) {
        User user = null;
        switch (ouser.getSource()) {
            case "qq":
                user = userService.findFistByQQOpenid(ouser.getOpenId());
                break;
            case "wechat":
                user = userService.findFistByWxOpenid(ouser.getOpenId());
                break;
            case "weibo":
                user = userService.findFistByWeiboOpenid(ouser.getOpenId());
                break;
            case "github":
                user = userService.findFistByGithubOpenid(ouser.getOpenId());
                break;
            case "gitee":
                user = userService.findFistByGiteeOpenid(ouser.getOpenId());
                break;
            case "dingding":
                user = userService.findFistByDingdingOpenid(ouser.getOpenId());
                break;
            default:
                redirect("/user/login");
                return;
        }

        if (user == null) {
            user = UserInterceptor.getThreadLocalUser();
            if (user != null) {
                user.setAvatar(ouser.getAvatar());
                user.setNickname(ouser.getNickname());
                openidService.saveOrUpdate(user.getId(), ouser.getSource(), ouser.getOpenId());
                userService.update(user);
            }
        }

        if (user == null) {
            user = new User();
            user.setAvatar(ouser.getAvatar());
            user.setNickname(ouser.getNickname());
            user.setCreateSource(ouser.getSource());
            user.setCreated(new Date());
            user.setGender(ouser.getGender());
            user.setSalt(HashKit.generateSaltForSha256());
            user.setLogged(new Date());

            // 是否启用邮件验证
            boolean emailValidate = JPressOptions.getAsBool("reg_email_validate_enable");
            if (emailValidate) {
                user.setStatus(User.STATUS_REG);
            } else {
                user.setStatus(User.STATUS_OK);
            }

            //强制用户状态为未激活
            boolean isNotActivate = JPressOptions.getAsBool("reg_users_is_not_activate");
            if (isNotActivate) {
                user.setStatus(User.STATUS_REG);
            }

            Object id = userService.save(user);
            openidService.saveOrUpdate(id, ouser.getSource(), ouser.getOpenId());

        }

        CookieUtil.put(this, JPressConsts.COOKIE_UID, user.getId());
        String gotoUrl = JPressOptions.get("login_goto_url", "/ucenter");
        redirect(gotoUrl);
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
            case "weibo":
                return createWeiboConnector();
            case "github":
                return createGithubConnector();
            case "gitee":
                return createGiteeConnector();
            case "dingding":
                return createDingdingConnector();
        }

        return null;
    }

    private OauthConnector createDingdingConnector() {
        boolean enable = JPressOptions.getAsBool("login_dingding_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_dingding_appkey");
        String appsecret = JPressOptions.get("login_dingding_appsecret");
        return new DingdingConnector(appkey, appsecret);
    }

    private OauthConnector createGiteeConnector() {
        boolean enable = JPressOptions.getAsBool("login_gitee_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_gitee_appkey");
        String appsecret = JPressOptions.get("login_gitee_appsecret");
        return new OSChinaConnector(appkey, appsecret);
    }

    private OauthConnector createGithubConnector() {
        boolean enable = JPressOptions.getAsBool("login_github_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_github_appkey");
        String appsecret = JPressOptions.get("login_github_appsecret");
        return new GithubConnector(appkey, appsecret);
    }

    private OauthConnector createWeiboConnector() {
        boolean enable = JPressOptions.getAsBool("login_weibo_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_weibo_appkey");
        String appsecret = JPressOptions.get("login_weibo_appsecret");
        return new WeiboConnector(appkey, appsecret);
    }


    private OauthConnector createQQConnector() {
        boolean enable = JPressOptions.getAsBool("login_qq_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_qq_appkey");
        String appsecret = JPressOptions.get("login_qq_appsecret");
        return new QQConnector(appkey, appsecret);
    }

    private OauthConnector createWechatConnector() {
        boolean enable = JPressOptions.getAsBool("login_wechat_enable");
        if (enable == false) {
            return null;
        }

        String appkey = JPressOptions.get("login_wechat_appkey");
        String appsecret = JPressOptions.get("login_wechat_appsecret");
        return new WechatConnector(appkey, appsecret);
    }


}
