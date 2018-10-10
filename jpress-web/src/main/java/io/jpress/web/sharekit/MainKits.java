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
package io.jpress.web.sharekit;

import io.jpress.JPressOptions;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */
public class MainKits {

    public static String blankCount(Integer count) {
        if (count == null || count == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    public static String escape(String html) {
        if (html == null || html.trim().length() == 0) {
            return "";
        }
        return StringEscapeUtils.escapeHtml(html);
    }


    public static Object option(String key) {
        String value = JPressOptions.get(key);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return value;
    }


}
