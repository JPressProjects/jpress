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
package io.jpress.module.article;

import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.core.ext.field.SmartField;
import io.jpress.core.ext.field.SmartFieldRender;
import io.jpress.module.article.model.Article;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章字段
 */
public class ArticleFields {

    private List<SmartField> fields = new ArrayList<>();
    private static ArticleFields me = new ArticleFields();

    private ArticleFields() {
        initDefaultFields();
    }

    private void initDefaultFields() {

        fields.add(new SmartField("summary",
                "文章摘要",
                "article.summary",
                "请输入",
                SmartField.TYPE_TEXTAREA,
                null,
                null,
                null,
                10).addAttr("rows", 4));

        fields.add(new SmartField("meta_keywords",
                "SEO关键字",
                "article.meta_keywords",
                "请输入",
                SmartField.TYPE_TEXTAREA,
                null,
                null,
                null,
                20).addAttr("rows", 2));

        fields.add(new SmartField("meta_description",
                "SEO描述",
                "article.meta_description",
                "请输入",
                SmartField.TYPE_TEXTAREA,
                null,
                null,
                null,
                30).addAttr("rows", 2));

        fields.add(new SmartField("flag",
                "标识",
                "article.flag",
                "请输入",
                SmartField.TYPE_INPUT,
                null,
                null,
                null,
                40));

        fields.add(new SmartField("link_to",
                "外链",
                "article.link_to",
                "请输入",
                SmartField.TYPE_INPUT,
                null,
                null,
                "填写外链后，浏览文章将会跳转到此链接。",
                50));

        fields.add(new SmartField().setOrderNo(60).setRender(new CommentEnableRender()));


        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));

    }

    public static ArticleFields me() {
        return me;
    }

    public void addField(SmartField field) {
        fields.add(field);
        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));
    }

    public void removeField(String id) {
        if (StrUtil.isBlank(id)) return;
        fields.removeIf(field -> id.equals(field.getId()));
        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));
    }

    public List<SmartField> getFields() {
        return fields;
    }

    public String render() {
        StringBuilder s = new StringBuilder();
        for (SmartField field : fields) {
            s.append(field.render());
        }
        return s.toString();
    }

    static class CommentEnableRender implements SmartFieldRender {
        String template = "" +
                "<div class=\"form-group\">\n" +
                "    <label class=\"col-sm-2 control-label\">允许评论</label>\n" +
                "    <div class=\"col-sm-6\">\n" +
                "        <input type=\"checkbox\" {checked} class=\"switchery\"\n" +
                "               data-for=\"comment_status\" value=\"true\">\n" +
                "        <input type=\"hidden\" id=\"comment_status\"\n" +
                "               name=\"article.comment_status\">\n" +
                "    </div>\n" +
                "</div>";

        @Override
        public String onRender(SmartField field, Object value) {
            Article article = JbootControllerContext.get().getAttr("article");
            String checked = (article == null || article.isCommentEnable())
                    ? "checked"
                    : "";
            return template.replace("{checked}", checked);
        }
    }
}
