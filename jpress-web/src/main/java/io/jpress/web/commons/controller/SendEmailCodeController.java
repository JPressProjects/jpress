package io.jpress.web.commons.controller;

import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.web.base.AdminControllerBase;

@RequestMapping("/commons/getemailcode")
public class SendEmailCodeController  extends AdminControllerBase {

    public void index(){

        String emailAddr = getPara("email");
        if (!StrUtil.isEmail(emailAddr)) {
            renderFailJson("您输入的邮箱地址有误。");
            return;
        }

        Email email = Email.create();

        email.subject("这是一封来至 JPress 的测试邮件");
        email.content("恭喜您，收到此邮件，证明您在 JPress 后台配置的邮件可用。");
        email.to(emailAddr);

        SimpleEmailSender ses = new SimpleEmailSender();
        if (!ses.isEnable()) {
            renderFailJson("您未开启邮件功能，无法发送。");
            return;
        }

        if (!ses.isConfigOk()) {
            renderFailJson("未配置正确，smtp 或 用户名 或 密码 为空。");
            return;
        }

        if (!ses.send(email)) {
            renderFailJson("未配置正确，smtp 或 用户名 或 密码 错误。");
            return;
        }

        renderOkJson();
    }
}
