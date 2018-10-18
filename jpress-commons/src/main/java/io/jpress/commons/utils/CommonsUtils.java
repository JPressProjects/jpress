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
package io.jpress.commons.utils;

import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class CommonsUtils {

    public static String maxLength(String content, int maxLength) {
        if (StrUtils.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength 必须大于 0 ");
        }

        return content.length() <= maxLength ? content :
                content.substring(0, maxLength);

    }

    /**
     * 防止 model 存储关于 xss 相关代码
     *
     * @param model
     */
    public static void preventingXssAttacks(Model model, String... ignoreAttr) {
        String[] attrNames = model._getAttrNames();
        for (String attrName : attrNames) {

            if (ArrayUtils.contains(ignoreAttr, attrName)) {
                continue;
            }

            Object value = model.get(attrName);

            if (value != null && value instanceof String) {
                model.set(attrName, escapeHtml(value.toString()));
            }
        }
    }

    public static String escapeHtml(String content) {

        if (StrUtils.isBlank(content)) {
            return content;
        }

        /**
         "&lt;" represents the < sign.
         "&gt;" represents the > sign.
         "&amp;" represents the & sign.
         "&quot; represents the " mark.
         */

        return unEscapeHtml(content)
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&#39;")
                .replace("&", "&amp;")
                .replace("\"", "&quot;");
    }

    public static String unEscapeHtml(String content) {

        if (StrUtils.isBlank(content)) {
            return content;
        }

        return content
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&quot;", "\"");
    }


    public static void main(String[] args) {
        String script = "<script>alert(\"abc\");</script>";
        String script1 = "&amp;lt;script&amp;gt;alert(&quot;abc&quot;);&amp;lt;/script&amp;gt;";
        System.out.println(escapeHtml(script));
        System.out.println(escapeHtml(escapeHtml(script)));
        System.out.println(escapeHtml(script1));
        System.out.println(escapeHtml(unEscapeHtml(script1)));
    }
}
