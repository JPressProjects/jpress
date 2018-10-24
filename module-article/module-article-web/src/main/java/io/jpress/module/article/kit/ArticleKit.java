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
package io.jpress.module.article.kit;

import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jpress.commons.email.Email;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.service.OptionService;

import java.util.HashMap;
import java.util.Map;

public class ArticleKit {

    /**
     * 发送邮件给管理员，告知网站有新的评论了
     *
     * @param article
     * @param comment
     */
    public static void doNotifyAdministratorByEmail(Article article, ArticleComment comment) {

        OptionService optionService = Jboot.bean(OptionService.class);

        Boolean enable = optionService.findAsBoolByKey("article_comment_email_notify_enable");
        if (enable == null || enable == false) {
            // do nothing
            return;
        }

        String emailTemplate = optionService.findByKey("article_comment_email_notify_template");
        String sendTo = optionService.findByKey("article_comment_email_notify_address");

        Map<String, Object> paras = new HashMap();
        paras.put("article", article);
        paras.put("comment", comment);

        String content = Engine.use().getTemplateByString(emailTemplate).renderToString(paras);
        Email email = Email.create();
        email.content(content);
        email.subject("有人评论你的文章：" + article.getTitle());
        email.to(sendTo);
        email.send();
    }

}
