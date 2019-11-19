package io.jpress.module.article.sitemap;


import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.core.install.Installer;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.sitemap.SitemapManager;

public class ArticleSitemapProviderBuilder implements JbootEventListener {

    private static ArticleSitemapProviderBuilder me = new ArticleSitemapProviderBuilder();

    private ArticleSitemapProviderBuilder() {
    }

    public static ArticleSitemapProviderBuilder me() {
        return me;
    }


    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }


        int pageSize = 100;

        ArticleService articleService = Aop.get(ArticleService.class);
        Page<Article> page = articleService.paginateInNormal(1, pageSize);

        SitemapManager.me().addProvider(new ArticlesSitemapProvider("article_1", page.getList()));

        int totalPage = page.getTotalPage();
        if (totalPage >= 2) {
            for (int i = 2; i < totalPage; i++) {
                Page<Article> articlePage = articleService.paginateInNormal(i, pageSize);
                SitemapManager.me().addProvider(new ArticlesSitemapProvider("article_" + i, articlePage.getList()));
            }
        }
    }

    @Override
    public void onEvent(JbootEvent event) {
        init();
    }


}
