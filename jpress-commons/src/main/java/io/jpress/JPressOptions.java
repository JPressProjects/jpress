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
package io.jpress;

import io.jboot.utils.StrUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 常量
 * @Package io.jpress
 */
public class JPressOptions {

    private static final Map<String, String> options = new HashMap<>();

    public static void set(String key, String value) {
        options.put(key, value);
    }

    public static String get(String key) {
        return options.get(key);
    }

    public static boolean getAsBool(String key) {
        return Boolean.parseBoolean(options.get(key));
    }

    public static String getAppUrlSuffix() {
        boolean fakeStaticEnable = getAsBool(JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == false) {
            return "";
        }

        String suffix = get(JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? "" : suffix;
    }

}
