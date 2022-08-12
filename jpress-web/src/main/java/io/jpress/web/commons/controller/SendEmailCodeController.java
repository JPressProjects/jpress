package io.jpress.web.commons.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jboot.web.validate.Regex;
import io.jpress.JPressOptions;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.EmailKit;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.web.base.ControllerBase;

import javax.validation.constraints.Pattern;

@RequestMapping("/commons/getemailcode")
public class SendEmailCodeController  extends ControllerBase {

    @Inject
    private CaptchaService captchaService;


    /**
     * 发送邮箱验证码
     * @param emailAddr
     * @param captchaVO
     */
    public void index(@Pattern(regexp = Regex.EMAIL) @JsonBody("email") String emailAddr, @JsonBody CaptchaVO captchaVO) {

        ResponseModel validResult = captchaService.verification(captchaVO);

        if (validResult != null && validResult.isSuccess()) {
            if (!StrUtil.isEmail(emailAddr)) {
                renderFailJson("您输入的邮箱地址有误。");
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

            //获取关于邮箱的配置内容
            //邮件标题
            String subject = JPressOptions.get("reg_email_validate_title");
            //生成验证码
            String code = EmailKit.generateCode();


            Email email = Email.create();
            email.subject(subject);
            email.content(" 验证码："+code+"，用于注册/登录，10分站内有效。");
            email.to(emailAddr);

            //发送邮箱验证码
            boolean sendOk = EmailKit.sendEmailCode(emailAddr, code, email);
            if (sendOk) {
                renderJson(Ret.ok().set("message", "邮箱验证码发送成功，请手机查看"));
            } else {
                renderJson(Ret.fail().set("message", "邮箱验证码实发失败，请联系管理员"));
            }
        } else {
            renderFailJson("验证错误");
        }
    }
}
