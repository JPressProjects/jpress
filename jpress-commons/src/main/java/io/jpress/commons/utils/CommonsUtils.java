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
package io.jpress.commons.utils;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtil;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class CommonsUtils {


    public static String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(9999 - 1000 + 1) + 1000);
    }

    public static void quietlyClose(AutoCloseable... autoCloseables) {
        for (AutoCloseable closeable : autoCloseables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }


    public static String maxLength(String content, int maxLength) {
        return maxLength(content, maxLength, null);
    }


    public static String maxLength(String content, int maxLength, String suffix) {
        if (StrUtil.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength 必须大于 0 ");
        }

        if (StrUtil.isNotBlank(suffix)) {
            return content.length() <= maxLength
                    ? content :
                    content.substring(0, maxLength) + suffix;

        } else {
            return content.length() <= maxLength
                    ? content :
                    content.substring(0, maxLength);

        }
    }

    public static String removeSuffix(String url) {

        int indexOf = url.indexOf(".");

        if (indexOf == -1) {
            return url;
        }

        return url.substring(0, indexOf);
    }

    /**
     * 防止 model 存储关于 xss 相关代码
     *
     * @param model
     */
    public static void escapeModel(Model model, String... ignoreAttrs) {
        Set<String> attrNames = CPI.getModifyFlag(model);
        for (String attr : attrNames) {

            if (containsAttr(ignoreAttrs, attr)) {
                continue;
            }

            Object value = model.get(attr);

            if (value instanceof String) {
                model.set(attr, StrUtil.escapeHtml(value.toString()));
            }
        }
    }

    public static boolean containsAttr(String[] attrs, String attr) {
        if (attrs == null || attrs.length == 0 || attr == null) {
            return false;
        }
        for (String s : attrs) {
            if (attr.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        System.out.println(generateCode());
    }
}
