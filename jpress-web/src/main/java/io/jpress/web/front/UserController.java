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


    /**
     * 用户信息页面
     */
    public void index() {

    }

    /**
     * 用户登录页面
     */
    public void login() {
        renderText("登录页面");
    }

    /**
     * 用户注册页面
     */
    public void register() {

    }

    /**
     * 退出登录
     */
    public void logout() {
        EncryptCookieUtils.remove(this, JPressConstants.COOKIE_UID);
        redirect("/user/login");
    }


}
