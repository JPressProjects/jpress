/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @Title: JPress 配置
 */
public class JPressOptions {

    private static final Log LOG = Log.getLog(JPressOptions.class);

    private static OptionStore store = OptionStore.defaultOptionStore;
    private static List<OptionChangeListener> listeners = new ArrayList<>();
    private static OptionNotifier notifier = null;


    public static void set(String key, String value) {
        if (StrUtil.isBlank(key)) {
            return;
        }

        key = key.toLowerCase();

        String oldValue = store.get(key);
        if (Objects.equals(value, oldValue)) {
            return;
        }

        store.put(key, value);


        for (OptionChangeListener listener : listeners) {
            try {
                listener.onChanged(key, value, oldValue);
            } catch (Throwable ex) {
                LOG.error(ex.toString(), ex);
            }
        }

        if (notifier != null) {
            notifier.notifyOptionSet(key, value);
        }

        doFinishedChanged(key, value, oldValue);
    }


    public static String get(String key) {
        return store.get(key.toLowerCase());
    }


    public static String get(String key, String defaultvalue) {
        String v = get(key);
        return StrUtil.isBlank(v) ? defaultvalue : v;
    }

    public static boolean getAsBool(String key) {
        return getAsBool(key, false);
    }

    public static boolean getAsBool(String key, boolean defaultValue) {
        String value = get(key);
        return StrUtil.isBlank(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    @Deprecated
    public static boolean isTrueOrEmpty(String key) {
        return getAsBool(key, true);
    }

    public static Integer getAsInt(String key) {
        String value = get(key);
        return StrUtil.isBlank(value) ? null : Integer.parseInt(value);
    }


    public static int getAsInt(String key, int defaultValue) {
        String value = get(key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            LOG.warn(ex.toString(), ex);
            return defaultValue;
        }
    }


    public static Float getAsFloat(String key) {
        String value = get(key);
        return StrUtil.isBlank(value) ? null : Float.parseFloat(value);
    }

    public static float getAsFloat(String key, float defaultValue) {
        String value = get(key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            LOG.warn(ex.toString(), ex);
            return defaultValue;
        }
    }

    public static void addListener(OptionChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(OptionChangeListener listener) {
        listeners.remove(listener);
    }

    public static OptionNotifier getNotifier() {
        return notifier;
    }

    public static void setNotifier(OptionNotifier notifier) {
        JPressOptions.notifier = notifier;
    }

    public static String getCDNDomain() {
        boolean cdnEnable = getAsBool(JPressConsts.OPTION_CDN_ENABLE);
        if (!cdnEnable) {
            return StrUtil.EMPTY;
        }

        return get(JPressConsts.OPTION_CDN_DOMAIN, StrUtil.EMPTY);
    }

    public static String getResDomain() {
        String cdnDomain = getCDNDomain();
        return cdnDomain == null ? get(JPressConsts.OPTION_WEB_DOMAIN) : cdnDomain;
    }

    public interface OptionChangeListener {
        void onChanged(String key, String newValue, String oldValue);
    }


    private static final String indexStyleKey = "index_style";

    private static void doFinishedChanged(String key, String value, String oldValue) {
        if (indexStyleKey.equals(key)) {
            indexStyleValue = value;
        }

        //伪静态的是否启用
        else if (JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE.equals(key)) {
            fakeStaticEnable = "true".equalsIgnoreCase(value);
        }

        //伪静态后缀
        else if (JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX.equals(key)) {
            fakeStaticSuffix = value;
        }

        //是否开启模板预览功能
        else if (JPressConsts.OPTION_WEB_TEMPLATE_PREVIEW_ENABLE.equals(key)) {
            templatePreviewEnable = "true".equalsIgnoreCase(value);
        }

        //是否开启模板预览功能
        else if (JPressConsts.OPTION_WEB_FLAT_URL_ENABLE.equals(key)) {
            templatePreviewEnable = "true".equalsIgnoreCase(value);
        }
    }

    private static String indexStyleValue = null;

    public static String getIndexStyle() {
        return indexStyleValue;
    }


    private static boolean fakeStaticEnable = false;
    private static String fakeStaticSuffix = "";

    public static String getAppUrlSuffix() {
        if (!fakeStaticEnable || StrUtil.isBlank(fakeStaticSuffix)) {
            return StrUtil.EMPTY;
        }
        return fakeStaticSuffix;
    }


    private static boolean templatePreviewEnable = false;

    public static boolean isTemplatePreviewEnable() {
        return templatePreviewEnable;
    }

    public static void setTemplatePreviewEnable(boolean templatePreviewEnable) {
        JPressOptions.templatePreviewEnable = templatePreviewEnable;
    }


    private static boolean flatUrlEnable = false;

    public static boolean isFlatUrlEnable() {
        return flatUrlEnable;
    }

    public static void setFlatUrlEnable(boolean flatUrlEnable) {
        JPressOptions.flatUrlEnable = flatUrlEnable;
    }

    public static OptionStore getStore() {
        return store;
    }

    public static void setStore(OptionStore store) {
        JPressOptions.store = store;
    }

    public interface OptionStore {

        String get(String key);

        void put(String key, String value);

        void remove(String key);

        OptionStore defaultOptionStore = new OptionStore() {

            private final Map<String, String> cache = new ConcurrentHashMap<>();

            @Override
            public String get(String key) {
                return cache.get(key);
            }

            @Override
            public void put(String key, String value) {
                if (StrUtil.isBlank(value)) {
                    remove(key);
                } else {
                    cache.put(key, value);
                }
            }

            @Override
            public void remove(String key) {
                cache.remove(key);
            }
        };

    }

    public interface OptionNotifier {
        void notifyOptionSet(String key, String value);
    }


}
