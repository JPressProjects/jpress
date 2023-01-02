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

import io.jboot.utils.StrUtil;
import io.jpress.core.support.smartfield.SmartField;


public class RenderKit {

    public static String render(String template, SmartField field, Object value) {
        if (StrUtil.isBlank(template)) {
            return template;
        }

        template = replace(template, "{label}", field.getLabel());
        template = replace(template, "{id}", field.getId());
        template = replace(template, "{name}", field.getName());
        template = replace(template, "{placeholder}", field.getPlaceholder());
        template = replace(template, "{value}", value == null ? field.getValue() : value);
        template = replace(template, "{helpText}", field.getHelpText());
        template = replace(template, "{attrs}", field.getAttrs());

        return template;
    }

    public static String replace(String template, String target, Object content) {
        return template.replace(target, content == null ? StrUtil.EMPTY : content.toString());
    }
}
