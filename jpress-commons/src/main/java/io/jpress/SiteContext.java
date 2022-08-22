package io.jpress;

import com.jfinal.core.Controller;
import com.jfinal.kit.Func;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;

public class SiteContext {

    private static ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void setSiteId(Long siteId) {
        TL.set(siteId);
    }

    public static Long getSiteId() {
        Long siteId = TL.get();
        return siteId == null ? 0L : siteId;
    }

    public static void removeSiteId() {
        TL.remove();
    }

    public static String getSitePath() {
        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            return "";
        }

        Model site = controller.getAttr("SITE");
        if (site == null) {
            return "";
        }

        String path = site.getStr("bind_path");
        return StrUtil.isNotBlank(path) ? path : "";
    }


    public static void execInMainSite(Func.F00 callable) {
        Long currentSiteId = getSiteId();
        if (currentSiteId == 0) {
            callable.call();
        } else {
            JbootCache cache = JbootCacheManager.me().getCache();
            try {
                setSiteId(0L);
                cache.setCurrentCacheNamePrefix("site0:");
                callable.call();
            } finally {
                setSiteId(currentSiteId);
                cache.setCurrentCacheNamePrefix("site" + currentSiteId + ":");
            }
        }
    }

    public static <T> T execInMainSite(Func.F01<T> callable) {
        Long currentSiteId = getSiteId();
        if (currentSiteId == 0) {
            return callable.call();
        } else {
            JbootCache cache = JbootCacheManager.me().getCache();
            try {
                setSiteId(0L);
                cache.setCurrentCacheNamePrefix("site0:");
                return callable.call();
            } finally {
                setSiteId(currentSiteId);
                cache.setCurrentCacheNamePrefix("site" + currentSiteId + ":");
            }
        }
    }


}
