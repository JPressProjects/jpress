package io.jpress.commons.email;

import com.jfinal.log.Log;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.email
 */

public class Email {

    private static final Log LOG = Log.getLog(Email.class);

    private String[] to = null;
    private String[] cc = null;
    private String subject = null;
    private String content = null;

    public static Email create() {
        return new Email();
    }

    public Email to(String... toEmails) {
        this.to = toEmails;
        return this;
    }

    public Email cc(String... ccEmails) {
        this.cc = ccEmails;
        return this;
    }

    public Email subject(String subject) {
        this.subject = subject;
        return this;
    }

    public Email content(String content) {
        this.content = content;
        return this;
    }

    public String[] getTo() {
        return to;
    }

    public String[] getCc() {
        return cc;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public void send() {
        send(new SimplerEmailSender());
    }

    public void send(IEmailSender sender) {
        try {
            sender.send(this);
        } catch (Throwable ex) {
            LOG.error(ex.toString(), ex);
        }
    }

    public static void main(String[] args) {
        Email.create().subject("这是邮件标题").content("这是邮件内容~~~~~~").to("1506615067@qq.com").send();
    }
}

