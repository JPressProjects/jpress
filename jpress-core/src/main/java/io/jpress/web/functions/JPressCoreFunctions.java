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
package io.jpress.web.functions;

import com.google.common.collect.Lists;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.JPressOptions;
import io.jpress.commons.bean.RenderList;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */
public class JPressCoreFunctions {

    public static String escape(String html) {
        if (html == null || html.trim().length() == 0) {
            return "";
        }
        return StrUtil.escapeHtml(html);
    }

    public static boolean hasAddon(String id) {
        AddonInfo addonInfo = AddonManager.me().getAddonInfo(id);
        return addonInfo != null && addonInfo.isStarted();
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


    public static Object option(String key, Object defaulValue) {

        String value = JPressOptions.get(key);

        if (StrUtil.isBlank(value)) {
            value = defaulValue == null ? "" : defaulValue.toString();
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return value;
    }


    public static List<String> linesOption(String key) {
        String value = JPressOptions.get(key);
        if (StrUtil.isBlank(value)) {
            return Lists.newArrayList();
        }

        String[] lines = value.split("\n");
        List<String> list = new ArrayList<>();
        for (String line : lines) {
            list.add(line.trim());
        }
        return list;
    }


    public static List<RenderList> linesOption(String key, String split) {
        String value = JPressOptions.get(key);
        if (StrUtil.isBlank(value)) {
            return Lists.newArrayList();
        }

        String[] lines = value.split("\n");
        List<RenderList> list = new ArrayList<>();
        for (String line : lines) {
            RenderList<String> items = new RenderList<>();
            String[] lineItems = line.trim().split(split);
            for (String lineItem : lineItems) {
                items.add(lineItem.trim());
            }
            list.add(items);
        }
        return list;
    }


    public static boolean isImage(String path) {
        return StrUtil.isNotBlank(path) && (AttachmentUtils.isImage(path) || path.toLowerCase().endsWith(".ico"));
    }


    public static String para(String key) {
        return para(key, null);
    }

    public static String para(String key, String defaultValue) {
        return JbootControllerContext.get().get(key, defaultValue);
    }


}
