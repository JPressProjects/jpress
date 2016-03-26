package io.jpress.notify.email;

public class Email {

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
		EmailSenderFactory.createSender().send(this);
	}

	public static void main(String[] args) {
		Email.create()
			.subject("这是邮件标题")
			.content("这是邮件内容~~~~~~")
			.to("1506615067@qq.com")
			.send();
	}

}
