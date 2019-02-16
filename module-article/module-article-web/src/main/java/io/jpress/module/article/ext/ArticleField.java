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
package io.jpress.module.article.ext;

import io.jboot.web.controller.JbootControllerContext;
import io.jpress.module.article.model.Article;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章字段
 */
public class ArticleField {

    public static final String TYPE_INPUT = "input";
    public static final String TYPE_TEXTAREA = "textarea";
    public static final String TYPE_SELECT = "select";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_SWITCH = "switch";


    private String lable;               //标题
    private String fieldName;           //组件的Name属性，若是以 article. 开头，则表示是文章的默认字段，否则是扩展字段
    private String placeholder;         //占位符
    private String type;                //类型
    private String value;               //值，多个值用英文逗号隔开，checkbox、select 支持多个值
    private String valueText;           //每个值对应的显示内容，例如 option 有 value 属性和其显示的具体内容
    private Map<String, String> attrs;  //其他的属性，例如 "row = 3"

    private int orderNo;                //排序字段
    private ArticleFieldRender render;  //自定义自己的render，自己没有render的时候，才会通过 ArticleFieldRenderFactory 去获取

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public void addAttr(String attrName, String attrValue) {
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put(attrName, attrValue);
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public ArticleFieldRender getRender() {
        return render == null
                ? ArticleFieldRenderFactory.getRender(type)
                : render;
    }

    public void setRender(ArticleFieldRender render) {
        this.render = render;
    }

    public String render() {
        Article article = JbootControllerContext.get().getAttr("article");
        return getRender().onRender(article, this);
    }
}
