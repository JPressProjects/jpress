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
package io.jpress.core.wechat;

import com.jfinal.aop.Aop;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.utils.ClassScanner;
import io.jpress.JPressOptions;
import io.jpress.core.install.Installer;
import io.jpress.service.OptionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
public class WechatAddonManager implements JbootEventListener {

    private static WechatAddonManager me = new WechatAddonManager();
    private static final String OPTION_PREFIX = "wechat-addon-enable:";

    private WechatAddonManager() {
    }

    public static WechatAddonManager me() {
        return me;
    }

    /**
     * 所有插件
     */
    private Map<String, WechatAddonInfo> allWechatAddons = new ConcurrentHashMap();

    /**
     * 已经启用的插件
     */
    private Map<String, WechatAddonInfo> enableWechatAddons = new ConcurrentHashMap<>();


    private OptionService optionService;

    /**
     * 初始化的主要逻辑
     * <p>
     * 1. 扫描当前所有的微信插件
     * 2. 查看该微信插件是否开启
     */
    public void init() {

        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        optionService = Aop.get(OptionService.class);

        List<Class<WechatAddon>> classes = ClassScanner.scanSubClass(WechatAddon.class, true);
        if (classes == null || classes.isEmpty()) {
            return;
        }

        for (Class<WechatAddon> addonClass : classes) {
            WechatAddonConfig wechatAddonConfig = addonClass.getDeclaredAnnotation(WechatAddonConfig.class);
            if (wechatAddonConfig == null) {
                continue;
            }

            WechatAddonInfo addon = new WechatAddonInfo(wechatAddonConfig, addonClass);
            addWechatAddon(addon);
        }

        doEnableAddons();
    }

    private void doEnableAddons(){
        for (Map.Entry<String,WechatAddonInfo> entry : allWechatAddons.entrySet()) {
            Boolean enable = JPressOptions.getAsBool(OPTION_PREFIX + entry.getKey());
            if (enable) {
                enableWechatAddons.put(entry.getKey(),entry.getValue());
            }
        }
    }


    public void addWechatAddon(WechatAddonInfo wechatAddon) {
        allWechatAddons.put(wechatAddon.getId(), wechatAddon);
    }

    public void deleteWechatAddon(String id){
        allWechatAddons.remove(id);
        enableWechatAddons.remove(id);
    }

    public List<WechatAddonInfo> getWechatAddons() {
        return new ArrayList<>(allWechatAddons.values());
    }

    public Collection<WechatAddonInfo> getEnableWechatAddons() {
        return enableWechatAddons.values();
    }

    public void doCloseAddon(String id) {
        optionService.saveOrUpdate(OPTION_PREFIX + id, "false");
        JPressOptions.set(OPTION_PREFIX + id, "false");
        enableWechatAddons.remove(id);
    }

    public void doEnableAddon(String id) {
        optionService.saveOrUpdate(OPTION_PREFIX + id, "true");
        JPressOptions.set(OPTION_PREFIX + id, "true");

        if (enableWechatAddons.containsKey(id)){
            return;
        }

        WechatAddonInfo addon = allWechatAddons.get(id);
        if (addon != null) {
            enableWechatAddons.put(id,addon);
        }
    }

    public boolean isEnable(String id) {
        return enableWechatAddons.containsKey(id);
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
