package io.jpress;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Func;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;

import javax.servlet.http.HttpServletRequest;

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

    public static String getSiteURL() {
        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            return "";
        }

        Model site = controller.getAttr("SITE");
        if (site == null) {
            return "";
        }

        String domain = site.getStr("bind_domain");
        if (StrUtil.isNotBlank(domain)) {
            HttpServletRequest request = controller.getRequest();
            domain = request.getScheme() + "://" + domain.trim();
        } else {
            domain = "";
        }

        String path = site.getStr("bind_path");
        if (path != null) {
            path = path.trim();
        } else {
            path = "";
        }

        return domain + JFinal.me().getContextPath() + path;
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
        if (path != null) {
            path = path.trim();
        } else {
            path = "";
        }

        return path;
    }


    public static void execInMainSite(Func.F00 callable) {
        execInSite(callable, 0L);
    }

    public static <T> T execInMainSite(Func.F01<T> callable) {
        return execInSite(callable, 0L);
    }

    public static void execInSite(Func.F00 callable, Long siteId) {
        Long currentSiteId = getSiteId();
        if (currentSiteId.equals(siteId)) {
            callable.call();
        } else {
            JbootCache cache = JbootCacheManager.me().getCache();
            try {
                setSiteId(siteId);
                cache.setThreadCacheNamePrefix("site" + siteId);
                callable.call();
            } finally {
                setSiteId(currentSiteId);
                cache.setThreadCacheNamePrefix("site" + currentSiteId);
            }
        }
    }

    public static <T> T execInSite(Func.F01<T> callable, Long siteId) {
        Long currentSiteId = getSiteId();
        if (currentSiteId.equals(siteId)) {
            return callable.call();
        } else {
            JbootCache cache = JbootCacheManager.me().getCache();
            try {
                setSiteId(siteId);
                cache.setThreadCacheNamePrefix("site" + siteId);
                return callable.call();
            } finally {
                setSiteId(currentSiteId);
                cache.setThreadCacheNamePrefix("site" + currentSiteId);
            }
        }
    }


}
