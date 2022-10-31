package io.jpress.web.handler;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
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
        SiteInfo siteInfo = SiteManager.me().matchSite(target, request, response);
        if (siteInfo != null) {

            //非后台路径，如果通过语言等匹配到了站点，跳转到相应站点
            if (!target.startsWith("/admin") && !siteInfo.isSiteAction(target, request)) {
                HandlerKit.redirect(siteInfo.getUrl(request.getScheme()), request, response, isHandled);
                return;
            }

            request.setAttribute(JPressConsts.ATTR_SITE_ID, siteInfo.getSiteId());
            request.setAttribute("SITE", siteInfo);

            if (StrUtil.isNotBlank(siteInfo.getBindPath())
                    && target.startsWith(siteInfo.getBindPath())) {
                if (target.length() == siteInfo.getBindPath().length()) {
                    target = "/";
                } else {
                    target = target.substring(siteInfo.getBindPath().length());
                }
            }

            request.setAttribute("SPATH", StrUtil.defaultIfBlank(siteInfo.getBindPath(), ""));
            JPressHandler.setCurrentTarget(target);
            SiteContext.setSiteId(siteInfo.getSiteId());
        }


        //设置缓存前缀
        JbootCache cache = JbootCacheManager.me().getCache();
        try {
            cache.setThreadCacheNamePrefix("site" + SiteContext.getSiteId());
            next.handle(target, request, response, isHandled);
        } finally {
            cache.clearThreadCacheNamePrefix();
            SiteContext.removeSiteId();
        }
    }


}
