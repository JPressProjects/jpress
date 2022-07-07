package io.jpress.web.handler;

import com.jfinal.handler.Handler;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.SiteContext;
import io.jpress.core.site.SiteManager;
import io.jpress.model.SiteInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SiteHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        // 匹配站点方式
        // 1、根据域名匹配
        // 2、根据绑定二级目录匹配
        // 3、更加 cookie 信息匹配
        SiteInfo siteInfo = SiteManager.me().matchedSite(target, request);
        if (siteInfo != null) {
            request.setAttribute(JPressConsts.ATTR_SITE_ID, siteInfo.getSiteId());
            request.setAttribute("CSITE", siteInfo);
            if (StrUtil.isNotBlank(siteInfo.getBindPath())
                    && target.startsWith(siteInfo.getBindPath())) {
                if (target.length() == siteInfo.getBindPath().length()) {
                    target = "/";
                } else {
                    target = target.substring(siteInfo.getBindPath().length());
                }
            }
            SiteContext.setSiteId(siteInfo.getSiteId());
        }


        //设置缓存前缀
        JbootCache cache = JbootCacheManager.me().getCache();
        try {
            cache.setCurrentCacheNamePrefix("site" + SiteContext.getSiteId() + ":");
            next.handle(target, request, response, isHandled);
        } finally {
            cache.removeCurrentCacheNamePrefix();
            SiteContext.removeSiteId();
        }
    }


}
