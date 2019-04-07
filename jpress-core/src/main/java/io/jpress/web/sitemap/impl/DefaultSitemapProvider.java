package io.jpress.web.sitemap.impl;

import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 用户显示 http://127.0.0.1:8080/sitemap/new.xml 的数据
 */
public class DefaultSitemapProvider implements SitemapProvider {

    private List<NewestSitemapProvider> newestSitemapProviders = new ArrayList<>();

    public DefaultSitemapProvider() {
        List<Class<NewestSitemapProvider>> cls = ClassScanner.scanSubClass(NewestSitemapProvider.class, true);
        if (cls != null && cls.size() > 0) {
            cls.forEach(c -> newestSitemapProviders.add(ClassUtil.newInstance(c)));
        }
    }

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public Date getLastmod() {
        return new Date();
    }

    @Override
    public List<Sitemap> getSitemaps() {
        List<Sitemap> list = new ArrayList<>();
        String domain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN, "");
        String webIndex = domain + "/";
        Sitemap indexSitemap = new Sitemap(webIndex, new Date(), Sitemap.CHANGEFREQ_ALWAYS, 1f);
        list.add(indexSitemap);
        newestSitemapProviders.forEach(provider -> {
            List<Sitemap> sitemaps = provider.getSitemaps();
            if (sitemaps != null) list.addAll(provider.getSitemaps());
        });
        list.sort(Comparator.comparing(Sitemap::getLastmod));
        return list;
    }
}
