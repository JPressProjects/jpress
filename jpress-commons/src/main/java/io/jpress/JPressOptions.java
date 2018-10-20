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

import com.jfinal.log.Log;
import io.jboot.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 常量
 * @Package io.jpress
 */
public class JPressOptions {

    private static final Log LOG = Log.getLog(JPressOptions.class);
    private static final Map<String, String> options = new ConcurrentHashMap<>();
    private static final List<OptionChangeListener> LISTENERS = new ArrayList<>();

    public static void set(String key, String value) {
        if (StrUtils.isBlank(key)) {
            return;
        }


        String oldValue = options.get(key);
        if (Objects.equals(value, oldValue)) {
            return;
        }

        if (StrUtils.isBlank(value)) {
            options.remove(key);
        } else {
            options.put(key, value);
        }


        for (OptionChangeListener listener : LISTENERS) {
            try {
                listener.onChanged(key, value, oldValue);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }
    }

    public static String get(String key) {
        return options.get(key);
    }

    public static boolean getAsBool(String key) {
        return Boolean.parseBoolean(options.get(key));
    }

    public static int getAsInt(String key, int defaultValue) {
        String value = get(key);
        if (StrUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static float getAsFloat(String key, float defaultValue) {
        String value = get(key);
        if (StrUtils.isBlank(value)) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    public static void addListener(OptionChangeListener listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(OptionChangeListener listener) {
        LISTENERS.remove(listener);
    }

    public static String getAppUrlSuffix() {
        boolean fakeStaticEnable = getAsBool(JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == false) {
            return "";
        }

        String suffix = get(JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? "" : suffix;
    }

    public static String getCDNDomain() {
        boolean cdnEnable = getAsBool(JPressConsts.OPTION_CDN_ENABLE);
        if (cdnEnable == false) {
            return null;
        }

        String cdnDomain = get(JPressConsts.OPTION_CDN_DOMAIN);
        return StrUtils.isBlank(cdnDomain) ? null : cdnDomain;
    }

    public static interface OptionChangeListener {
        public void onChanged(String key, String newValue, String oldValue);
    }

}
