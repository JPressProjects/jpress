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
package io.jpress.web.commons.email;

import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.commons.sms.NonSmsSender;
import io.jpress.commons.sms.SmsMessage;
import io.jpress.commons.sms.SmsSender;
import io.jpress.commons.sms.SmsSenderFactory;
import io.jpress.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class AdminMessageSender {

    private static final Log LOG = Log.getLog(AdminMessageSender.class);

    private static ExecutorService emailTreadPool = NamedThreadPools.newFixedThreadPool(3, "admin-email-sender");
    private static ExecutorService smsTreadPool = NamedThreadPools.newFixedThreadPool(3, "admin-sms-sender");
    private static ExecutorService wechatTreadPool = NamedThreadPools.newFixedThreadPool(3, "admin-wechat-sender");


    public static Ret sendEmail(String title, String content, String cc, List<User> users) {

        SimpleEmailSender ses = new SimpleEmailSender();

        if (!ses.isEnable()) {
            return Ret.fail().set("message", "您未开启邮件功能，无法发送。");
        }

        if (!ses.isConfigOk()) {
            return Ret.fail().set("message", "未配置正确，smtp 或 用户名 或 密码 为空。");
        }

        if (ArrayUtil.isNotEmpty(users)) {
            for (User user : users) {
                String emailAddr = user.getEmail();

                if (StrUtil.isBlank(emailAddr) || !StrUtil.isEmail(emailAddr)) {
                    continue;
                }

                emailTreadPool.execute(() -> {

                    Map<String, Object> paras = new HashMap();
                    paras.put("user", user);

                    String emailTitle = Engine.use().getTemplateByString(title).renderToString(paras);
                    String emailContent = Engine.use().getTemplateByString(content).renderToString(paras);


                    Email email = Email.create();
                    email.subject(emailTitle);
                    email.content(emailContent);
                    email.to(emailAddr);

                    if (!ses.send(email)) {
                        LOG.error("send email error , email " + emailAddr + " title :" + emailTitle);
                    }
                });
            }
        }

        Set<String> emailAddrs = StrUtil.splitToSet(cc, ",");
        if (ArrayUtil.isNotEmpty(emailAddrs)) {
            for (String emailAddr : emailAddrs) {

                if (StrUtil.isBlank(emailAddr) || !StrUtil.isEmail(emailAddr)) {
                    continue;
                }
                emailTreadPool.execute(() -> {

                    Email email = Email.create();
                    email.subject(title);
                    email.content(content);
                    email.to(emailAddr);

                    if (!ses.send(email)) {
                        LOG.error("send email error , email " + emailAddr + " title :" + title);
                    }
                });
            }
        }

        return Ret.ok();
    }


    public static Ret sendWechat(String templateId, String url, String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, List<User> users, String cc) {


        return Ret.ok();
    }


    public static Ret sendSms(String smsTemplate, String smsSign, String cc, List<User> users) {

        SmsSender smsSender = SmsSenderFactory.createSender();
        if (smsSender instanceof NonSmsSender) {
            return Ret.fail().set("message", "您未开启短信功能，无法发送。");
        }


        if (ArrayUtil.isNotEmpty(users)) {
            for (User user : users) {
                String mobile = user.getMobile();

                if (StrUtil.isBlank(mobile) || !StrUtil.isMobileNumber(mobile)) {
                    continue;
                }

                emailTreadPool.execute(() -> {

                    SmsMessage message = new SmsMessage();
                    message.setMobile(mobile);
                    message.setTemplate(smsTemplate);
                    message.setSign(smsSign);

                    if (!smsSender.send(message)) {
                        LOG.error("send sms error from admin , mobile " + mobile);
                    }
                });
            }
        }

        Set<String> mobiles = StrUtil.splitToSet(cc, ",");
        if (ArrayUtil.isNotEmpty(mobiles)) {
            for (String mobile : mobiles) {

                if (StrUtil.isBlank(mobile) || !StrUtil.isMobileNumber(mobile)) {
                    continue;
                }
                emailTreadPool.execute(() -> {

                    SmsMessage message = new SmsMessage();
                    message.setMobile(mobile);
                    message.setTemplate(smsTemplate);
                    message.setSign(smsSign);

                    if (!smsSender.send(message)) {
                        LOG.error("send sms error from admin , mobile " + mobile);
                    }
                });
            }
        }

        return Ret.ok();
    }


}
