package io.jpress.web.front;

import com.jfinal.aop.Clear;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.JPressConstants;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/user")
public class UserController extends TemplateControllerBase {

    private static final String default_user_template = "/WEB-INF/views/ucenter/user.html";
    private static final String default_user_login_template = "/WEB-INF/views/ucenter/user_login.html";
    private static final String default_user_register_template = "/WEB-INF/views/ucenter/user_register.html";

    @Inject
    private UserService userService;


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

    @Clear
    @EmptyValidate({
            @Form(name = "user", message = "账号不能为空"),
            @Form(name = "pwd", message = "密码不能为空")
    })
    public void doLogin(String user, String pwd) {

        Ret ret = StrUtils.isEmail(user)
                ? userService.loginByEmail(user, pwd)
                : userService.loginByUsername(user, pwd);

        if (ret.isOk()) {
            User userModel = ret.getAs("user");
            EncryptCookieUtils.put(this, JPressConstants.COOKIE_UID, userModel.getId());
        }

        renderJson(ret);
    }

    /**
     * 用户注册页面
     */
    public void register() {
        render("user_register.html", default_user_register_template);
    }


    public void doRegister() {


        String username = getPara("username");
        String email = getPara("email");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtils.isBlank(username)) {
            renderJson(Ret.fail().set("message", "username must not be empty").set("errorCode", 1));
            return;
        }

        if (StrUtils.isBlank(email)) {
            renderJson(Ret.fail().set("message", "email must not be empty").set("errorCode", 2));
            return;
        }

        if (StrUtils.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "password must not be empty").set("errorCode", 3));
            return;
        }

        if (StrUtils.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "confirm password must not be empty").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "confirm password must equals password").set("errorCode", 5));
            return;
        }

        if (validateCaptcha("captcha") == false) {
            renderJson(Ret.fail().set("message", "captcha is error").set("errorCode", 6));
            return;
        }


        User user = userService.findFistByUsername(username);
        if (user != null) {
            renderJson(Ret.fail().set("message", "username exist").set("errorCode", 10));
            return;
        }

        user = userService.findFistByEmail(email);
        if (user != null) {
            renderJson(Ret.fail().set("message", "email exist").set("errorCode", 11));
            return;
        }

        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        user = new User();
        user.setUsername(username);
        user.setEmail(email.toLowerCase());
        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());
        user.setStatus(User.STATUS_REG);

        userService.save(user);

        renderJson(Ret.ok());
    }

    /**
     * 退出登录
     */
    public void logout() {
        EncryptCookieUtils.remove(this, JPressConstants.COOKIE_UID);
        redirect("/user/login");
    }


}
