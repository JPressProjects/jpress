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
package io.jpress.web.commons;

import com.jfinal.template.Engine;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.email.Email;
import io.jpress.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JPressEmailSender {

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);


    public static void sendEmailForUserRegisterValidate(User user) {
        boolean emailValidate = JPressOptions.getAsBool("reg_email_validate_enable");
        if (emailValidate == false) return;

        AuthCode authCode = AuthCode.newCode(user.getId());
        AuthCodeKit.save(authCode);

        String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        String url = webDomain + "/user/activate?id=" + authCode.getId();


        String title = JPressOptions.get("reg_email_validate_title");
        String template = JPressOptions.get("reg_email_validate_template");
        Map<String, Object> paras = new HashMap();
        paras.put("user", user);
        paras.put("code", authCode.getCode());
        paras.put("url", url);

        String content = Engine.use().getTemplateByString(template).renderToString(paras);

        Email email = Email.create();
        email.content(content);
        email.subject(title);
        email.to(user.getEmail());

        sendEmail(email);
    }

    public static void sendEmail(Email email) {
        fixedThreadPool.execute(() -> email.send());
    }


}
