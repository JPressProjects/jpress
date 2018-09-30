package io.jpress.web.front;

import io.jboot.utils.EncryptCookieUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/user")
public class UserController extends TemplateControllerBase {

    private static final String default_user_template = "/WEB-INF/views/ucenter/user.html";
    private static final String default_user_login_template = "/WEB-INF/views/ucenter/user_login.html";
    private static final String default_user_register_template = "/WEB-INF/views/ucenter/user_regsiter.html";


    /**
     * 用户信息页面
     */
    public void index() {
        render("user_detail.html", default_user_template);
    }

    /**
     * 用户登录页面
     */
    public void login() {
        render("user_login.html", default_user_login_template);
    }

    /**
     * 用户注册页面
     */
    public void register() {
        render("user_register.html", default_user_register_template);
    }

    /**
     * 退出登录
     */
    public void logout() {
        EncryptCookieUtils.remove(this, JPressConstants.COOKIE_UID);
        redirect("/user/login");
    }


}
