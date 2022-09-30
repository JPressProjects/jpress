package io.jpress.web.render;

import com.jfinal.render.RedirectRender;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

public class JPressRedirectRender extends RedirectRender {

    public JPressRedirectRender(String url) {
        super(buildFullUrl(url));
    }

    public JPressRedirectRender(String url, boolean withQueryString) {
        super(buildFullUrl(url), withQueryString);
    }


    private static String buildFullUrl(String orginalUrl) {
        String webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        if (StrUtil.isBlank(webDomain)) {
            return orginalUrl;
        }

        if (!orginalUrl.contains("://") || orginalUrl.indexOf("://") > 5) {
            return webDomain + orginalUrl;
        }

        return orginalUrl;
    }
}
