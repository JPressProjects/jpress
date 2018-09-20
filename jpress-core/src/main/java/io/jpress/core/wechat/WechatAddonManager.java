package io.jpress.core.wechat;

import io.jboot.Jboot;
import io.jboot.utils.ClassScanner;
import io.jpress.service.OptionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
public class WechatAddonManager {

    private static WechatAddonManager me = new WechatAddonManager();
    private static final String OPTION_PREFIX = "wechat_addon_enable_for_";

    private WechatAddonManager() {
    }

    public static WechatAddonManager me() {
        return me;
    }

    /**
     * 所有插件
     */
    private List<WechatAddon> allWechatAddons = new ArrayList<>();

    /**
     * 已经启用的插件
     */
    private List<WechatAddon> enableWechatAddons = new ArrayList<>();


    private OptionService optionService;

    public void init() {

        optionService = Jboot.bean(OptionService.class);

        List<Class<WechatAddonListener>> classes = ClassScanner.scanSubClass(WechatAddonListener.class, true);
        if (classes == null || classes.isEmpty()) {
            return;
        }

        for (Class<WechatAddonListener> listenerClass : classes) {
            WechatAddonConfig wechatAddonConfig = listenerClass.getDeclaredAnnotation(WechatAddonConfig.class);
            if (wechatAddonConfig == null) {
                continue;
            }

            WechatAddon addon = createWechatAddon(wechatAddonConfig, listenerClass);
            addWechatAddon(addon);
        }

        for (WechatAddon addon : allWechatAddons) {
            Boolean enable = optionService.findAsBoolByKey(OPTION_PREFIX + addon.getId());
            if (enable != null && enable == true) {
                enableWechatAddons.add(addon);
            }
        }
    }

    private WechatAddon createWechatAddon(WechatAddonConfig config, Class<WechatAddonListener> listenerClass) {

        WechatAddon wechatAddon = new WechatAddon();
        wechatAddon.setId(config.id());
        wechatAddon.setAuthor(config.author());
        wechatAddon.setAuthorWebsite(config.authorWebsite());
        wechatAddon.setDescription(config.description());
        wechatAddon.setListenerClazz(listenerClass.getCanonicalName());
        wechatAddon.setTitle(config.title());
        wechatAddon.setUpdateUrl(config.updateUrl());
        wechatAddon.setVersion(config.version());
        wechatAddon.setVersionCode(config.versionCode());

        Jboot.injectMembers(wechatAddon);
        return wechatAddon;
    }


    public void addWechatAddon(WechatAddon wechatAddon) {
        allWechatAddons.add(wechatAddon);
    }

    public List<WechatAddon> getWechatAddons() {
        return allWechatAddons;
    }

    public List<WechatAddon> getEnableWechatAddons() {
        return enableWechatAddons;
    }

    public void doCloseAddon(String id) {
        optionService.saveOrUpdate(OPTION_PREFIX + id, "false");
        enableWechatAddons.remove(new WechatAddon(id));
    }

    public void doEnableAddon(String id) {
        optionService.saveOrUpdate(OPTION_PREFIX + id, "true");
        for (WechatAddon addon : enableWechatAddons) {
            if (addon.getId().equals(id)) {
                return;
            }
        }

        for (WechatAddon addon : allWechatAddons) {
            if (addon.getId().equals(id)) {
                enableWechatAddons.add(addon);
                break;
            }
        }
    }

    public boolean isEnable(WechatAddon addon) {
        Boolean bool =  enableWechatAddons.contains(addon);
        return bool;
    }

}
