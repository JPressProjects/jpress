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
package io.jpress.web.commons.finance;

import com.jfinal.template.Engine;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.email.Email;
import io.jpress.commons.sms.SmsKit;
import io.jpress.model.PaymentRecord;
import io.jpress.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class PayNotifytKit {

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3,"pay-notify");

    public static void notify(PaymentRecord payment, User user) {
        byEmail(payment, user);
        bySms(payment);
    }


    private static void bySms(PaymentRecord payment) {
        boolean enable = JPressOptions.getAsBool("pay_notify_sms_enable");
        if (enable) {
            fixedThreadPool.execute(() -> doSendSms());
        }
    }

    private static void doSendSms() {
        String mobile = JPressOptions.get("web_mater_mobile");
        String template = JPressOptions.get("pay_notify_sms_template");
        String sign = JPressOptions.get("pay_notify_sms_sign");

        if (!StrUtil.areNotEmpty(mobile, template, sign)) {
            return;
        }

        Set<String> mobiles = StrUtil.splitToSet(mobile, ",");
        for (String nobileNumber : mobiles) {
            SmsKit.sendSms(nobileNumber, template, sign);
        }

    }


    public static void byEmail(PaymentRecord product, User user) {
        boolean enable = JPressOptions.getAsBool("pay_notify_email_enable");
        if (enable) {
            fixedThreadPool.execute(() -> doSendEmail(product, user));
        }
    }


    private static void doSendEmail(PaymentRecord payment,  User user) {

        String emailTemplate = JPressOptions.get("pay_notify_email_template");
        String emailTitle = JPressOptions.get("pay_notify_email_title");
        String webMasterEmail = JPressOptions.get("web_mater_email");

        if (!StrUtil.areNotEmpty(emailTemplate, emailTitle, webMasterEmail)) {
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


}
