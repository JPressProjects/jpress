package io.jpress.module.page.sitemap;

import com.jfinal.aop.Inject;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class PageSitemapProvider implements SitemapProvider {

    @Inject
    private SinglePageService pageService;

    @Override
    public String getName() {
        return "page";
    }

    @Override
    public Date getLastmod() {
        List<Sitemap> sitemaps = getSitemaps();
        return sitemaps == null || sitemaps.isEmpty() ? null : sitemaps.get(0).getLastmod();
    }

    @Override
    public List<Sitemap> getSitemaps() {
        List<SinglePage> pageList = pageService.findAll();
        if (pageList == null || pageList.isEmpty()) {
            return null;
        }
        return pageList.stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }


}
