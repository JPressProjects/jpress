/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.commons.finance;

import com.jfinal.template.Engine;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.email.Email;
import io.jpress.commons.sms.SmsKit;
import io.jpress.model.PaymentRecord;
import io.jpress.model.User;
import io.jpress.web.wechat.WechatMsgUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrePayNotifytKit {

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    public static void doNotifyAdministrator(PaymentRecord payment, User user) {
        doNotifyAdministratorByEmail(payment, user);
        doNotifyAdministratorByWechat(payment, user);
        doNotifyAdministratorBySms(payment);
    }

    /**
     * 发送邮件给管理员，告知网站有新的评论了
     *
     * @param payment
     */

    public static void doNotifyAdministratorBySms(PaymentRecord payment) {
        boolean enable = JPressOptions.getAsBool("prepay_notify_sms_enable");
        if (enable) fixedThreadPool.execute(() -> doSendSms());
    }

    private static void doSendSms() {
        String mobile = JPressOptions.get("web_mater_mobile");
        String template = JPressOptions.get("prepay_notify_sms_template");
        String sign = JPressOptions.get("prepay_notify_sms_sign");

        if (!StrUtil.areNotEmpty(mobile, template, sign)) {
            return;
        }

        Set<String> mobiles = StrUtil.splitToSet(mobile, ",");
        for (String nobileNumber : mobiles) {
            SmsKit.sendSms(nobileNumber, template, sign);
        }

    }


    public static void doNotifyAdministratorByEmail(PaymentRecord payment, User user) {
        boolean enable = JPressOptions.getAsBool("prepay_notify_email_enable");
        if (enable) {
            fixedThreadPool.execute(() -> doSendEmail(payment, user));
        }
    }


    private static void doSendEmail(PaymentRecord payment, User user) {

        String emailTemplate = JPressOptions.get("prepay_notify_email_template");
        String emailTitle = JPressOptions.get("prepay_notify_email_title");
        String webMasterEmail = JPressOptions.get("web_mater_email");

        if (!StrUtil.areNotEmpty(emailTemplate, emailTemplate, webMasterEmail)) {
            return;
        }

        Map<String, Object> paras = new HashMap();
        paras.put("payment", payment);
        paras.put("user", user);

        String title = Engine.use().getTemplateByString(emailTitle).renderToString(paras);
        String content = Engine.use().getTemplateByString(emailTemplate).renderToString(paras);

        Email email = Email.create();
        email.content(content);
        email.subject(title);
        email.to(webMasterEmail.split(","));
        email.send();

    }


    public static void doNotifyAdministratorByWechat(PaymentRecord payment, User user) {
        boolean enable = JPressOptions.getAsBool("prepay_notify_wechat_enable");
        if (enable) {
            fixedThreadPool.execute(() -> doSendWechat(payment, user));
        }
    }


    private static void doSendWechat(PaymentRecord payment, User user) {

        String emailTemplate = JPressOptions.get("prepay_notify_wechat_template");
        String webMasterOpenId = JPressOptions.get("web_mater_wxopenid");

        if (!StrUtil.areNotEmpty(emailTemplate, emailTemplate)) {
            return;
        }

        Map<String, Object> paras = new HashMap();
        paras.put("payment", payment);
        paras.put("user", user);

        String content = Engine.use().getTemplateByString(emailTemplate).renderToString(paras);

        Set<String> openIds = StrUtil.splitToSet(webMasterOpenId, ",");
        for (String openId : openIds) {
            WechatMsgUtil.sendImageAsync(openId, content);
        }

    }


}
