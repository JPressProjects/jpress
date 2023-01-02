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
package io.jpress.core.support.smartfield.renders;

import io.jpress.core.support.smartfield.SmartField;
import io.jpress.core.support.smartfield.SmartFieldRender;

import java.util.Objects;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Switch 的渲染器
 */
public class SwitchRender implements SmartFieldRender {

//    protected static String template = "" +
//            "<div class=\"form-group row\">\n" +
//            "    <label class=\"col-sm-2 col-form-label\">{label}</label>\n" +
//            "    <div class=\"col-sm-6\">\n" +
//            "        <input type=\"checkbox\" {checked} class=\"switchery\"\n" +
//            "               data-for=\"{id}\" value=\"true\">\n" +
//            "        <p class=\"text-muted\">{helpText}</p>\n" +
//            "        <input type=\"hidden\" id=\"{id}\" name=\"{name}\">\n" +
//            "    </div>\n" +
//            "</div>";

    protected static String template = "" +
            "<div class=\"form-group row\">\n" +
            "    <label class=\"col-sm-9\" style=\"padding-top:7.5px;padding-left:15px\">{label}</label>\n" +
            "    <div class=\"col-sm-3\">\n" +
            "        <input type=\"checkbox\" {checked} class=\"switchery\"\n" +
            "               data-for=\"{id}\" value=\"true\">\n" +
            "        <p class=\"text-muted\">{helpText}</p>\n" +
            "        <input type=\"hidden\" id=\"{id}\" name=\"{name}\">\n" +
            "    </div>\n" +
            "</div>";

    @Override
    public String onRender(SmartField field, Object value) {
        String checked = (value == null && "true".equals(field.getValue()))
                ? "checked" : (Objects.equals("true", String.valueOf(value)) ? "checked" : "");
        return RenderKit.render(template, field, value).replace("{checked}", checked);
    }
}
