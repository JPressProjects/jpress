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
package io.jpress.module.article;

import io.jboot.utils.StrUtil;
import io.jpress.core.support.smartfield.SmartField;

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

        fields.add(new SmartField("order_number",
                "排序序号",
                "article.order_number",
                "请输入",
                SmartField.TYPE_INPUT,
                null,
                null,
                "文章列表会根据这个数值进行排序，越大越靠前。",
                50));


        fields.add(new SmartField("link_to",
                "外链",
                "article.link_to",
                "请输入",
                SmartField.TYPE_INPUT,
                null,
                null,
                "填写外链后，浏览文章将会跳转到此链接。",
                60));

        fields.add(new SmartField("created",
                "发布时间",
                "article.created",
                "请输入",
                SmartField.TYPE_DATETIME,
                null,
                null,
                null,
                61));


        fields.add(new SmartField("comment_status",
                "允许评论",
                "article.comment_status",
                "请输入",
                SmartField.TYPE_SWITCH,
                "true",
                null,
                null,
                70));


        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));

    }

    public static ArticleFields me() {
        return me;
    }

    public void addField(SmartField field) {
        removeField(field.getId()); //防止添加重复的Field
        fields.add(field);
        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));
    }

    public void removeField(String id) {
        if (StrUtil.isBlank(id)) {
            return;
        }
        fields.removeIf(field -> id.equals(field.getId()));
        fields.sort(Comparator.comparingInt(SmartField::getOrderNo));
    }

    public List<SmartField> getFields() {
        return fields;
    }

    public String render() {
        StringBuilder s = new StringBuilder();
        for (SmartField field : fields) {
            String html = field.render();
            if (html != null) {
                s.append(html);
            }
        }
        return s.toString();
    }

}
