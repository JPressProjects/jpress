package io.jpress.module.route.sitemap;

import io.jpress.module.route.model.TRoute;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旅游线路 sitemap
 *
 * @author Eric.Huang
 * @date 2019-04-08 22:53
 * @package io.jpress.module.route.sitemap
 **/

public class RouteSitemapProvider implements SitemapProvider {

    private String name;
    private List<TRoute> routeList;

    public RouteSitemapProvider() {
    }

    public RouteSitemapProvider(String name, List<TRoute> routeList) {
        this.name = name;
        this.routeList = routeList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getLastmod() {
        List<Sitemap> sitemaps = getSitemaps();
        return sitemaps == null || sitemaps.isEmpty() ? null : sitemaps.get(0).getLastmod();
    }

    @Override
    public List<Sitemap> getSitemaps() {
        if (routeList == null || routeList.isEmpty()) {
            return null;
        }
        return routeList.stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}
