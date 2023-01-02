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
package io.jpress.commons.email;

import com.jfinal.log.Log;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class SimpleEmailSender extends Authenticator implements EmailSender {

    private static final Log logger = Log.getLog(SimpleEmailSender.class);

    private String host;
    private String name;
    private String password;
    private boolean useSSL = true;
    private boolean enable = false;

    public SimpleEmailSender() {
        this.host = JPressOptions.get(JPressConsts.OPTION_CONNECTION_EMAIL_SMTP);
        this.name = JPressOptions.get(JPressConsts.OPTION_CONNECTION_EMAIL_ACCOUNT);
        this.password = JPressOptions.get(JPressConsts.OPTION_CONNECTION_EMAIL_PASSWORD);
        this.useSSL = JPressOptions.getAsBool(JPressConsts.OPTION_CONNECTION_EMAIL_SSL_ENABLE);
        this.enable = JPressOptions.getAsBool(JPressConsts.OPTION_CONNECTION_EMAIL_ENABLE);
    }

    private Message createMessage() {


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
        if (emails == null || emails.length == 0) {
            return null;
        }

        Set<Address> addresses = new HashSet<>();
        for (String email : emails) {
            if (StrUtil.isNotBlank(email)) {
                try {
                    addresses.add(new InternetAddress(email));
                } catch (AddressException e) {
                    continue;
                }
            }
        }
        return addresses.toArray(new Address[0]);
    }

    @Override
    public boolean send(Email email) {
        if (enable == false) {
            //do nothing
            return false;
        }

        Message message = createMessage();
        try {
            message.setSubject(email.getSubject());
            message.setContent(email.getContent(), "text/html;charset=utf-8");

            message.setRecipients(Message.RecipientType.TO, toAddress(email.getTo()));
            message.setRecipients(Message.RecipientType.CC, toAddress(email.getCc()));

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            logger.error("SimpleEmailSender send error", e);
        }

        return false;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isConfigOk() {
        return StrUtil.areNotEmpty(host, name, password);
    }
}
