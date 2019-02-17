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
package io.jpress.core.ext.field;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章字段
 */
public class ExtField {

    public static final String TYPE_INPUT = "input";
    public static final String TYPE_TEXTAREA = "textarea";
    public static final String TYPE_SELECT = "select";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_SWITCH = "switch";


    private String id;                  //ID
    private String label;               //标题
    private String name;                //组件的Name属性，若是以 article. 开头，则表示是文章的默认字段，否则是扩展字段
    private String placeholder;         //占位符
    private String type;                //类型
    private String value;               //值，多个值用英文逗号隔开，checkbox、select 支持多个值
    private String valueText;           //每个值对应的显示内容，例如 option 有 value 属性和其显示的具体内容
    private String helpText;            //帮助文本内容
    private Map<String, String> attrs;  //其他的属性，例如 "row = 3"

    private int orderNo;                //排序字段
    private ExtFieldRender render;  //自定义自己的render，自己没有render的时候，才会通过 ExtFieldRenderFactory 去获取

    public ExtField() {
    }

    public ExtField(String id, String label, String name, String placeholder, String type, String value, String valueText, String helpText, int orderNo) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.placeholder = placeholder;
        this.type = type;
        this.value = value;
        this.valueText = valueText;
        this.helpText = helpText;
        this.orderNo = orderNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return value == null ? "" : value;
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

    public String getHelpText() {
        return helpText == null ? "" : helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public String getAttrsAsString() {
        if (attrs == null || attrs.size() == 0) {
            return "";
        }

        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            s.append(entry.getKey().trim())
                    .append("=\"")
                    .append(entry.getValue() == null ? "" : entry.getValue().trim())
                    .append("\" ");
        }
        return s.toString();
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public ExtField addAttr(String attrName, Object attrValue) {
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put(attrName, String.valueOf(attrValue));
        return this;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public ExtField setOrderNo(int orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public ExtFieldRender getRender() {
        return render == null
                ? ExtFieldRenderFactory.getRender(type)
                : render;
    }

    public ExtField setRender(ExtFieldRender render) {
        this.render = render;
        return this;
    }

    public String render() {
        String name = this.name;
        if (StrUtil.isBlank(name)){
            return getRender().onRender(this,null);
        }
        Controller controller = JbootControllerContext.get();
        if (name.contains(".")) {
            String[] modelAndAttr = name.split("\\.");
            String modelName = modelAndAttr[0];
            String attr = modelAndAttr[1];
            Object object = controller.getAttr(modelName);
            if (object == null) {
                return getRender().onRender(this, null);
            } else if (object instanceof Model) {
                return getRender().onRender(this, ((Model) object).get(attr));
            } else {
                try {
                    Method method = object.getClass().getMethod("get" + StrKit.firstCharToUpperCase(attr));
                    Object data = method.invoke(object);
                    return getRender().onRender(this, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

        } else {
            return getRender().onRender(this, controller.getAttr(name));
        }
    }
}
