package io.jpress.module.article.service.sitemap;


import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.core.install.Installer;
import io.jpress.module.article.model.Article;
import io.jpress.web.sitemap.SitemapManager;

public class ArticleSitemapManager implements JbootEventListener {

    private static ArticleSitemapManager me = new ArticleSitemapManager();

    private ArticleSitemapManager() {
    }

    public static ArticleSitemapManager me() {
        return me;
    }


    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }
        build();
    }


    public void rebuild() {
        build();
    }


    private void build() {
        Long articleCount = Db.queryLong("select count(*) from article where `status` = ? ", Article.STATUS_NORMAL);
        if (articleCount != null && articleCount > 0) {
            int totalPage = (int) ((articleCount + 100) / 100);
            for (int i = 1; i <= totalPage; i++) {
                String name = "article_" + i;
                SitemapManager.me().addProvider(Aop.inject(new ArticleSitemapProvider(name, i)));
            }
        }
    }

    @Override
    public void onEvent(JbootEvent event) {
        init();
    }


}
