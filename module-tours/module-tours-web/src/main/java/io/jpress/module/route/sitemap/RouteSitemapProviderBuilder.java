package io.jpress.module.route.sitemap;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.core.install.Installer;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import io.jpress.web.sitemap.SitemapManager;

/**
 * 旅游线路 sitemap
 *
 * @author Eric.Huang
 * @date 2019-04-08 22:26
 * @package io.jpress.module.route.sitemap
 **/

public class RouteSitemapProviderBuilder implements JbootEventListener {

    private static RouteSitemapProviderBuilder me = new RouteSitemapProviderBuilder();

    private RouteSitemapProviderBuilder() {}

    public static RouteSitemapProviderBuilder me() {
        return me;
    }

    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        SitemapManager.me().addBuilder(() -> RouteSitemapProviderBuilder.this.build());
    }

    @Override
    public void onEvent(JbootEvent event) {
        init();
    }

    public void build() {
        int pageSize = 100;

        TRouteService routeService = Aop.get(TRouteService.class);
        Page<TRoute> page = routeService.paginateInNormal(1, pageSize);
        SitemapManager.me().addProvider(new RouteSitemapProvider("route_1", page.getList()));

        int totalPage = page.getTotalPage();
        if (totalPage >= 2) {
            for (int i = 2; i < totalPage; i++) {
                Page<TRoute> routePage = routeService.paginateInNormal(i, pageSize);
                SitemapManager.me().addProvider(new RouteSitemapProvider("route_1" + i, routePage.getList()));
            }
        }
    }
}
