package io.jpress.web.sitemap;

import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 用户显示 http://127.0.0.1:8080/sitemap/main.xml 的数据
 */
public class MainSitemapCreater implements SitemapProvider {

    private List<MainSitemapProvider> mainSitemapProviders = new ArrayList<>();

    public MainSitemapCreater() {
        List<Class<MainSitemapProvider>> cls = ClassScanner.scanSubClass(MainSitemapProvider.class, true);
        if (cls != null && cls.size() > 0) {
            cls.forEach(c -> {
                MainSitemapProvider provider = ClassUtil.newInstance(c);
                if (provider != null) {
                    mainSitemapProviders.add(provider);
                }
            });
        }
    }

    @Override
    public String getName() {
        return "main";
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


        mainSitemapProviders.forEach(provider -> {
            List<Sitemap> sitemaps = provider.getSitemaps();
            if (sitemaps != null) {
                list.addAll(provider.getSitemaps());
            }
        });

        list.sort(Comparator.comparing(Sitemap::getLastmod));

        return list;
    }
}
