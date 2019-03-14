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
package io.jpress.core.support.smartfield.renders;

import io.jboot.utils.StrUtil;
import io.jpress.core.support.smartfield.SmartField;
import io.jpress.core.support.smartfield.SmartFieldRender;

import java.util.Objects;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Textarea 的渲染器
 */
public class SelectRender implements SmartFieldRender {

    protected static String template1 = "" +
            "<div class=\"form-group\">\n" +
            "    <label class=\"col-sm-2 control-label\">{label}</label>\n" +
            "    <div class=\"col-sm-6\">\n" +
            "        <select class=\"form-control\" name=\"{name}\">\n";


    protected static String template2 = "" +
            "        </select>\n" +
            "        <p class=\"help-block\">{helpText}</p>\n" +
            "    </div>\n" +
            "</div>";

    @Override
    public String onRender(SmartField field, Object value) {
        if (field.getValue() == null || StrUtil.isBlank(field.getValue())) {
            return null;
        }
        String[] values = field.getValue().split(",");
        String[] texts = field.getValueText() == null ? null : field.getValue().split(",");

        int index = 0;
        StringBuilder options = new StringBuilder();
        for (String v : values) {
            options.append("<option value=\"")
                    .append(v) // value
                    .append("\" ")
                    .append(getSelectedText(v, value))
                    .append(" >")
                    .append(getText(texts, index++, v))
                    .append("</option>\n");
        }

        return RenderKit.render(template1, field, value) +
                options.toString() +
                RenderKit.render(template2, field, value);
    }

    private String getSelectedText(String v, Object value) {
        return Objects.equals(v, value) ? "selected" : "";
    }

    private String getText(String[] texts, int i, String v) {
        if (texts != null && texts.length > i) {
            return texts[i];
        }
        return v;
    }


}
