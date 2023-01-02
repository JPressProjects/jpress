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
package io.jpress.web.front;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.template.Engine;
import io.jboot.utils.CacheUtil;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jboot.web.validate.Regex;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.authcode.AuthCode;
import io.jpress.commons.authcode.AuthCodeKit;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.EmailKit;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.commons.sms.SmsKit;
import io.jpress.commons.utils.SessionUtils;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.commons.email.EmailSender;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/user")
public class UserController extends TemplateControllerBase {

    private static final String default_user_login_template = "/WEB-INF/views/ucenter/user_login.html";
    private static final String default_user_register_template = "/WEB-INF/views/ucenter/user_register.html";
    private static final String default_user_phone_register_template = "/WEB-INF/views/ucenter/user_phone_register.html";
    private static final String default_user_register_activate = "/WEB-INF/views/ucenter/user_activate.html";
    private static final String default_user_register_emailactivate = "/WEB-INF/views/ucenter/user_emailactivate.html";
    private static final String default_user_retrieve_password = "/WEB-INF/views/ucenter/user_retrieve_password.html";
    private static final String default_send_link_to_user = "/WEB-INF/views/ucenter/send_link_to_user.html";
    private static final String default_user_reset_password = "/WEB-INF/views/ucenter/user_reset_password.html";


    @Inject
    private UserService userService;

    @Inject
    private CaptchaService captchaService;




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

        pwd = getOriginalPara("pwd");

        if (StrUtil.isBlank(user) || StrUtil.isBlank(pwd)) {
            LogKit.error("你当前的 idea 或者 eclipse 配置有问题，请参考文档：http://www.jfinal.com/doc/3-3 进行配置");
            return;
        }

        if (JPressOptions.getAsBool("login_captcha_enable",true)) {
            String captcha = get("captcha");
            if (StrUtil.isBlank(captcha)){
                renderJson(Ret.fail().set("message", "验证码不能为空").set("errorCode", 7));
                return;
            }
            if (!validateCaptcha("captcha")) {
                renderJson(Ret.fail().set("message", "验证码不正确").set("errorCode", 7));
                return;
            }
        }

        User loginUser = userService.findByUsernameOrEmail(user);
        if (loginUser == null) {
            renderJson(Ret.fail("message", "用户名不正确。"));
            return;
        }

        Ret ret = userService.doValidateUserPwd(loginUser, pwd);

        if (ret.isOk()) {
            SessionUtils.record(loginUser.getId());
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
     * 手机号注册页面
     */
    public void phoneRegister() {
        render("user_phone_register.html", default_user_phone_register_template);
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

        if (!pwd.equals(confirmPwd)) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        if (StrUtil.isBlank(getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不能为空").set("errorCode", 6));
            return;
        }

        if (!EmailKit.validateCode(email,getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不正确").set("errorCode", 7));
            return;
        }


        User user = userService.findFirstByUsername(username);
        if (user != null) {
            renderJson(Ret.fail().set("message", "该用户名已经存在").set("errorCode", 10));
            return;
        }

        user = userService.findFirstByEmail(email);
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

    /**
     * 手机号注册
     */
    public void doPhoneRegister() {

        boolean regEnable = JPressOptions.isTrueOrEmpty("reg_enable");
        if (!regEnable) {
            renderJson(Ret.fail().set("message", "注册功能已经关闭").set("errorCode", 12));
            return;
        }

        String username = getPara("username");
        String phone = getPara("phone");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isBlank(username)) {
            renderJson(Ret.fail().set("message", "用户名不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtil.isBlank(phone)) {
            renderJson(Ret.fail().set("message", "手机号不能为空").set("errorCode", 2));
            return;
        }

        if (StrUtil.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (!pwd.equals(confirmPwd)) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        if (StrUtil.isBlank(getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不能为空").set("errorCode", 6));
            return;
        }

        if (!SmsKit.validateCode(phone,getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不正确").set("errorCode", 7));
            return;
        }

        User user = userService.findFirstByUsername(username);
        if (user != null) {
            renderJson(Ret.fail().set("message", "该用户名已经存在").set("errorCode", 10));
            return;
        }

        user = userService.findFirstByMobile(phone);
        if (user != null) {
            renderJson(Ret.fail().set("message", "该手机号已被注册").set("errorCode", 11));
            return;
        }

        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        user = new User();
        user.setUsername(username);
        user.setNickname(username);
        user.setRealname(username);
        user.setMobile(phone);
        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());

        user.setMobileStatus("ok" );

        user.setCreateSource(User.SOURCE_WEB_REGISTER);
        user.setAnonym(CookieUtil.get(this, JPressConsts.COOKIE_ANONYM));

        //是否启用短信验证
        boolean smsValidate = JPressOptions.getAsBool("reg_sms_validate_enable");
        if (smsValidate) {
            user.setStatus(User.STATUS_REG);
        }else {
            user.setStatus(User.STATUS_OK);
        }

        //强制用户状态为未激活
        boolean isNotActivate = JPressOptions.getAsBool("reg_users_is_not_activate");
        if (isNotActivate) {
            user.setStatus(User.STATUS_REG);
        }

        userService.save(user);

        renderJson(user != null ? OK : FAIL);
    }


    /**
     * 发送短信验证码
     * @param mobile
     * @param captchaVO
     */
    public void doSendLoginCode(@Pattern(regexp = Regex.MOBILE) @JsonBody("mobile") String mobile, @JsonBody CaptchaVO captchaVO) {
        ResponseModel validResult = captchaService.verification(captchaVO);
        if (validResult != null && validResult.isSuccess()) {
            String code = SmsKit.generateCode();
            String template = JPressOptions.get("reg_sms_validate_template");
            String sign = JPressOptions.get("reg_sms_validate_sign");

            boolean sendOk = SmsKit.sendCode(mobile, code, template, sign);

            if (sendOk) {
                renderJson(Ret.ok().set("message", "短信发送成功，请手机查看"));
            } else {
                renderJson(Ret.fail().set("message", "短信实发失败，请联系管理员"));
            }
        } else {
            renderFailJson("验证错误");
        }
    }



    /**
     * 找回密码
     */
    public void retrievePwd() {
        render("user_retrieve_password.html", default_user_retrieve_password);
    }

    /**
     * 发送重置密码的链接给用户
     */
    public void sendLink() {
        render("send_link_to_user.html", default_send_link_to_user);
    }

    /**
     * 重置密码
     */
    public void resetPwd() {
        String token = getPara("token");
        setAttr("token",token);

        Boolean isEmail = getParaToBoolean("isEmail");
        setAttr("isEmail",isEmail);

        render("user_reset_password.html", default_user_reset_password);
    }


    /**
     * 给邮箱账号发送重置密码的链接地址
     * @param emailAddr
     * @param captchaVO
     */
    public  void doSendResetPwdLinkToEmail(@Pattern(regexp = Regex.EMAIL) @JsonBody("email")  String emailAddr,@JsonBody  CaptchaVO captchaVO) {
        ResponseModel validResult = captchaService.verification(captchaVO);
        if (validResult != null && validResult.isSuccess()) {
            if (!StrUtil.isEmail(emailAddr)) {
                renderFailJson("您输入的邮箱地址有误。");
                return;
            }

            User user = userService.findFirstByEmail(emailAddr);
            if(user == null){
                renderJson(Ret.fail().set("message", "该邮箱未注册或已被删除").set("errorCode", 11));
                return;
            }

            SimpleEmailSender ses = new SimpleEmailSender();
            if (!ses.isEnable()) {
                renderFailJson("您未开启邮件功能，无法发送。");
                return;
            }

            if (!ses.isConfigOk()) {
                renderFailJson("未配置正确，smtp 或 用户名 或 密码 为空。");
                return;
            }

            //生成唯一不重复的字符串
            String token = UUID.randomUUID().toString();
            //token和邮箱绑定
            CacheUtil.put("email_token",token,emailAddr,60 * 60);
            CacheUtil.put("email_token",emailAddr,token,60 * 60);

            String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
            if (StrUtil.isBlank(webDomain)){
                webDomain = RequestUtil.getBaseUrl();
            }
            String url = webDomain + "/user/resetPwd?token=" + token+"&isEmail=true";

            String webName = JPressOptions.get(JPressConsts.ATTR_WEB_NAME);
            if (StrUtil.isNotBlank(webName)) {
                webName = webName+"：";
            }else{
                webName ="";
            }

            String title = webName + JPressOptions.get("reg_email_reset_pwd_title");
            String template = JPressOptions.get("reg_email_reset_pwd_template");

            String contentLink = null;
            if(StrUtil.isNotBlank(template)){
                Map<String, Object> paras = new HashMap();
                paras.put("user", user);
                paras.put("url", url);
                contentLink = Engine.use().getTemplateByString(template).renderToString(paras);
            }else{
                contentLink = "重置密码地址：<a href=\"" + url + "\">" + url + "</a>";
            }

            //获取关于邮箱的配置内容
            Email email = Email.create();
            email.subject(title);
            email.content(contentLink);


            email.to(emailAddr);
            String emailNumber = CacheUtil.get("email_token", token);

            //发送邮箱重置密码链接
            EmailKit.sendResetPwdLinkToEmail(email);
            renderJson(Ret.ok().set("message", "邮箱重置密码链接发送成功，请手机查看").set("email",emailNumber).set("isEmail",true));
        } else {
            renderFailJson("验证错误");
        }
    }


    /**
     * 重置密码 给手机账号发送验证码
     * @param mobile
     * @param captchaVO
     */
    public void doResetPwdSendCodeToMobile(@Pattern(regexp = Regex.MOBILE) @JsonBody("mobile") String mobile, @JsonBody CaptchaVO captchaVO) {
        ResponseModel validResult = captchaService.verification(captchaVO);
        if (validResult != null && validResult.isSuccess()) {
            //生成唯一不重复的字符串
            String token = UUID.randomUUID().toString();
            //token和电话号码绑定
            CacheUtil.put("mobile_token", token, mobile, 60 * 60);
            CacheUtil.put("mobile_token", mobile, token, 60 * 60);

            String code = SmsKit.generateCode();
            String template = JPressOptions.get("reg_sms_reset_pwd_template");
            String sign = JPressOptions.get("reg_sms_validate_sign");

            boolean sendOk = SmsKit.sendCode(mobile, code, template, sign);

            if (sendOk) {
                renderJson(Ret.ok().set("message", "短信验证码发送成功，请手机查看！").set("mobile", mobile).set("isEmail", false));
            } else {
                renderJson(Ret.fail().set("message", "短信实发失败，请联系管理员"));
            }

        } else {
            renderFailJson("验证错误");
        }
    }

    /**
     * 重置用户密码
     */
    public void doResetPassword() {

        String token = getPara("token") == null ? "" : getPara("token");
        //根据cacheName和token从缓存中获取数据value
        String email = CacheUtil.get("email_token", token) == null ? "": CacheUtil.get("email_token", token);
        String mobile = CacheUtil.get("mobile_token", token) == null ? "": CacheUtil.get("mobile_token", token);
        //根据缓存名称获取存入的token
        String emailToken = CacheUtil.get("email_token", email);
        String mobileToken = CacheUtil.get("mobile_token", mobile);

        Boolean isEmail = getParaToBoolean("type");

        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isBlank(token)) {
            renderJson(Ret.fail().set("message", "token不能为空，请重新发送").set("errorCode", 2));
            return;
        }
        if(isEmail && StrUtil.isNotBlank(emailToken) && !token.equals(emailToken)){
            renderJson(Ret.fail().set("message", "token是无效的，请重新发送！").set("errorCode", 5));
            return;
        }

        if (isEmail && StrUtil.isBlank(email)) {
            renderJson(Ret.fail().set("message", "邮箱不存在或已被删除").set("errorCode", 2));
            return;
        } else {
            email = email.toLowerCase();
        }

        if(!isEmail&& StrUtil.isNotBlank(mobileToken)  && !mobileToken.equals(token)){
            renderJson(Ret.fail().set("message", "token是无效的，请重新发送！").set("errorCode", 5));
            return;
        }

        if (!isEmail && StrUtil.isBlank(mobile)) {
            renderJson(Ret.fail().set("message", "手机号不存在或已被删除").set("errorCode", 2));
            return;
        }

        if (StrUtil.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (!pwd.equals(confirmPwd)) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        if(isEmail){
            User user = userService.findFirstByEmail(email);
            if (user != null) {

                String salt = HashKit.generateSaltForSha256();
                String hashedPass = HashKit.sha256(salt + pwd);

                user.setSalt(salt);
                user.setPassword(hashedPass);
                userService.update(user);
            }else{
                renderJson(Ret.fail().set("message", "邮箱不存在或已被删除").set("errorCode", 11));
                return;
            }
            renderJson(user != null ? OK.set("message","重置密码成功") : FAIL.set("message","重置密码失败"));
        }else{
            User user = userService.findFirstByMobile(mobile);
            if (user != null) {

                String salt = HashKit.generateSaltForSha256();
                String hashedPass = HashKit.sha256(salt + pwd);

                user.setSalt(salt);
                user.setPassword(hashedPass);
                userService.update(user);
            }else{
                renderJson(Ret.fail().set("message", "该手机号不存在或已被删除").set("errorCode", 11));
                return;
            }
            renderJson(user != null ? OK.set("message","重置密码成功") : FAIL.set("message","重置密码失败"));
        }
    }


    /**
     * 通过手机号找回密码
     * 验证输入的验证码是否正确
     * 验证手机号是否注册
     */
    public void validateCodeToResetPwd(){

        String phone = getPara("mobile");
        Object token = CacheUtil.get("mobile_token", phone);

        if (StrUtil.isBlank(getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不能为空").set("errorCode", 6));
            return;
        }

        if (!SmsKit.validateCode(phone,getPara("captcha"))) {
            renderJson(Ret.fail().set("message", "验证码不正确").set("errorCode", 7));
            return;
        }


        User user = userService.findFirstByMobile(phone);

        if (user == null) {
            renderJson(Ret.fail().set("message", "手机号未注册或已被删除，请重新输入！").set("errorCode", 11));
            return;
        }else{

            String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
            if (StrUtil.isBlank(webDomain)){
                webDomain = RequestUtil.getBaseUrl();
            }
            String url = webDomain + "/user/resetPwd?token=" + token+"&isEmail=false";
            renderJson(Ret.ok().set("message","校验正确，可以进行密码重置！").set("url",url));
        }


    }



}




