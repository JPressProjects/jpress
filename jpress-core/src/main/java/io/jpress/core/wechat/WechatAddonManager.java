package io.jpress.core.wechat;

import io.jboot.Jboot;
import io.jboot.utils.ClassScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
public class WechatAddonManager {

    private static WechatAddonManager me = new WechatAddonManager();

    private WechatAddonManager() {
    }

    public static WechatAddonManager me() {
        return me;
    }

    private List<WechatAddon> wechatAddons = new ArrayList<>();

    public void init() {
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
        wechatAddons.add(wechatAddon);
    }

    public List<WechatAddon> getWechatAddons() {
        return wechatAddons;
    }

    public List<WechatAddon> getEnableWechatAddons() {
        return wechatAddons;
    }

}
