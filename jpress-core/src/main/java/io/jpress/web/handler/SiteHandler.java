package io.jpress.web.handler;

import com.jfinal.handler.Handler;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
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
        SiteInfo siteInfo = SiteManager.me().matchedSiteId(target, request);
        if (siteInfo != null) {
            request.setAttribute(JPressConsts.ATTR_SITE_ID, siteInfo.getSiteId());
            if (StrUtil.isNotBlank(siteInfo.getBindPath())
                    && target.startsWith(siteInfo.getBindPath())) {
                target = target.substring(siteInfo.getBindPath().length());
            }
        }

        next.handle(target, request, response, isHandled);
    }


}
