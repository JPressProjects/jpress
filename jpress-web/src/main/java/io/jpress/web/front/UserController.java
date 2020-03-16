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

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.sms.SmsKit;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.commons.authcode.AuthCode;
import io.jpress.commons.authcode.AuthCodeKit;
import io.jpress.web.commons.email.EmailSender;

import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/user")
public class UserController extends TemplateControllerBase {

    private static final String default_user_login_template = "/WEB-INF/views/ucenter/user_login.html";
    private static final String default_user_register_template = "/WEB-INF/views/ucenter/user_register.html";
    private static final String default_user_register_activate = "/WEB-INF/views/ucenter/user_activate.html";
    private static final String default_user_register_emailactivate = "/WEB-INF/views/ucenter/user_emailactivate.html";

    @Inject
    private UserService userService;


    /**
     * 用户信息页面
     */
    public void index() {

        //不支持渲染用户详情
        if (hasTemplate("user_detail.html") == false) {
            renderError(404);
            return;
        }

        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }

        User user = userService.findById(id);
        if (user == null) {
            renderError(404);
            return;
        }

        setAttr("user", user.keepSafe());
        render("user_detail.html");
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

        if (StrUtil.isBlank(user) || StrUtil.isBlank(pwd)) {
            LogKit.error("你当前的 idea 或者 eclipse 可能有问题，请参考文档：http://www.jfinal.com/doc/3-3 进行配置");
            return;
        }

        User loginUser = userService.findByUsernameOrEmail(user);
        if (loginUser == null) {
            renderJson(Ret.fail("message", "用户名不正确。"));
            return;
        }

        Ret ret = userService.doValidateUserPwd(loginUser, pwd);

        if (ret.isOk()) {
            CookieUtil.put(this, JPressConsts.COOKIE_UID, loginUser.getId());
        }

        String gotoUrl = JPressOptions.get("login_goto_url", "/ucenter");
        ret.set("gotoUrl", gotoUrl);

        renderJson(ret);
    }

    /**
     * 用户注册页面
     */
    public void register() {
        render("user_register.html", default_user_register_template);
    }


    /**
     * 用户激活页面
     */
    public void activate() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderError(404);
            return;
        }

        AuthCode authCode = AuthCodeKit.get(id);
        if (authCode == null) {
            setAttr("code", 1);
            setAttr("message", "链接已经失效，可以尝试再次发送激活邮件");
            render("user_activate.html", default_user_register_activate);
            return;
        }

        User user = userService.findById(authCode.getUserId());
        if (user == null) {
            setAttr("code", 2);
            setAttr("message", "用户不存在或已经被删除");
            render("user_activate.html", default_user_register_activate);
            return;
        }

        user.setStatus(User.STATUS_OK);
        userService.update(user);

        setAttr("code", 0);
        setAttr("user", user);
        render("user_activate.html", default_user_register_activate);
    }


    /**
     * 邮件激活
     */
    public void emailactivate() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderError(404);
            return;
        }

        AuthCode authCode = AuthCodeKit.get(id);
        if (authCode == null) {
            setAttr("code", 1);
            setAttr("message", "链接已经失效，您可以尝试在用户中心再次发送激活邮件");
            render("user_emailactivate.html", default_user_register_emailactivate);
            return;
        }

        User user = userService.findById(authCode.getUserId());
        if (user == null) {
            setAttr("code", 2);
            setAttr("message", "用户不存在或已经被删除");
            render("user_emailactivate.html", default_user_register_emailactivate);
            return;
        }

        user.setEmailStatus(User.STATUS_OK);
        userService.update(user);

        setAttr("code", 0);
        setAttr("user", user);
        render("user_emailactivate.html", default_user_register_emailactivate);
    }


    public void doRegister() {

        boolean regEnable = JPressOptions.isTrueOrEmpty("reg_enable");
        if (!regEnable) {
            renderJson(Ret.fail().set("message", "注册功能已经关闭").set("errorCode", 12));
            return;
        }

        String username = getPara("username");
        String email = getPara("email");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isBlank(username)) {
            renderJson(Ret.fail().set("message", "用户名不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtil.isBlank(email)) {
            renderJson(Ret.fail().set("message", "邮箱不能为空").set("errorCode", 2));
            return;
        } else {
            email = email.toLowerCase();
        }

        if (StrUtil.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        if (StrUtil.isBlank(getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不能为空").set("errorCode", 6));
            return;
        }

        if (validateCaptcha("captcha") == false) {
            renderJson(Ret.fail().set("message", "验证码不正确").set("errorCode", 7));
            return;
        }

        String phoneNumber = getPara("phone");

        //是否启用短信验证
        boolean smsValidate = JPressOptions.getAsBool("reg_sms_validate_enable");
        if (smsValidate == true) {
            String paraCode = getPara("sms_code");
            if (SmsKit.validateCode(phoneNumber, paraCode) == false) {
                renderJson(Ret.fail().set("message", "短信验证码输入错误").set("errorCode", 7));
                return;
            }
        }

        User user = userService.findFistByUsername(username);
        if (user != null) {
            renderJson(Ret.fail().set("message", "该用户名已经存在").set("errorCode", 10));
            return;
        }

        user = userService.findFistByEmail(email);
        if (user != null) {
            renderJson(Ret.fail().set("message", "该邮箱已经存在").set("errorCode", 11));
            return;
        }

        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        user = new User();
        user.setUsername(username);
        user.setNickname(username);
        user.setRealname(username);
        user.setEmail(email.toLowerCase());
        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());

        user.setMobile(phoneNumber);
        user.setMobileStatus(smsValidate ? "ok" : null); // 如果 smsValidate == true，并走到此处，说明验证码已经验证通过了

        user.setCreateSource(User.SOURCE_WEB_REGISTER);
        user.setAnonym(CookieUtil.get(this, JPressConsts.COOKIE_ANONYM));

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

        Object userId = userService.save(user);

        if (userId != null && emailValidate) {
            EmailSender.sendForUserActivate(user);
        }

        renderJson(user != null ? OK : FAIL);
    }


}
