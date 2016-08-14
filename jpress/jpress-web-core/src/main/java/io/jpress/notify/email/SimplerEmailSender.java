/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.notify.email;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.jfinal.log.Log;

import io.jpress.model.query.OptionQuery;

public class SimplerEmailSender extends Authenticator implements IEmailSender {
	private static final Log logger = Log.getLog(SimplerEmailSender.class);

	private String host;
	private String name;
	private String password;

	private boolean useSSL = true;

	private void init() {
		this.host = OptionQuery.me().findValue("email_host");// "smtp.qq.com";
		this.name = OptionQuery.me().findValue("email_username");// "198819880@qq.com";
		this.password = OptionQuery.me().findValue("email_password");
		this.useSSL = Boolean.parseBoolean(OptionQuery.me().findValue("email_usessl"));
	}

	private Message createMessage() {

		init();

		Properties props = new Properties();

		props.setProperty("mail.transport.protocol", "smtp");

		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.port", "25");

		if (useSSL) {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.port", "465");
		}

		// error:javax.mail.MessagingException: 501 Syntax: HELO hostname
		props.setProperty("mail.smtp.localhost", "127.0.0.1");

		Session session = Session.getInstance(props, this);
		Message message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(MimeUtility.encodeText(name) + "<" + name + ">"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return message;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(name, password);
	}

	private static Address[] toAddress(String... emails) {
		if (emails == null || emails.length == 0)
			return null;

		Set<Address> addSet = new HashSet<Address>();
		for (String email : emails) {
			try {
				addSet.add(new InternetAddress(email));
			} catch (AddressException e) {
				continue;
			}
		}
		return addSet.toArray(new Address[0]);
	}

	@Override
	public void send(Email email) {
		Message message = createMessage();
		try {
			message.setSubject(email.getSubject());
			message.setContent(email.getContent(), "text/html;charset=utf-8");

			message.setRecipients(Message.RecipientType.TO, toAddress(email.getTo()));
			message.setRecipients(Message.RecipientType.CC, toAddress(email.getCc()));

			Transport.send(message);
		} catch (MessagingException e) {
			logger.error("SimplerEmailSender send error", e);
		}

	}
}
