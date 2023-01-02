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

import com.jfinal.template.Engine;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.authcode.AuthCode;
import io.jpress.commons.authcode.AuthCodeKit;
import io.jpress.commons.email.Email;
import io.jpress.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class EmailSender {

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3, "email-sender");


    /**
     * 用户注册时，发送邮件进行激活用户账号
     *
     * @param user
     */
    public static void sendForUserActivate(User user) {
        boolean emailValidate = JPressOptions.getAsBool("reg_email_validate_enable");
        if (emailValidate == false) {
            return;
        }

        if (StrUtil.isBlank(user.getEmail())) {
            return;
        }

        AuthCode authCode = AuthCode.newCode(user.getId());
        AuthCodeKit.save(authCode);

        String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        if (StrUtil.isBlank(webDomain)){
            webDomain = RequestUtil.getBaseUrl();
        }
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


    /**
     * 邮箱激活功能
     *
     * @param user
     */
    public static void sendForEmailActivate(User user) {

        AuthCode authCode = AuthCode.newCode(user.getId());
        AuthCodeKit.save(authCode);

        String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        String url = webDomain + "/user/emailactivate?id=" + authCode.getId();

        String webName = JPressOptions.get(JPressConsts.ATTR_WEB_NAME);
        if (webName == null) {
            webName = "";
        }

        String title = webName + "邮件激活";
        String content = "邮箱激活网址：<a href=\"" + url + "\">" + url + "</a>";

        Email email = Email.create();

        email.subject(title);
        email.content(content);
        email.to(user.getEmail());

        sendEmail(email);
    }

    public static void sendEmail(Email email) {
        fixedThreadPool.execute(() -> email.send());
    }


}
