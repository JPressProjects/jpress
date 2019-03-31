package io.jpress.module.article.sitemap;


import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.web.sitemap.Sitemap;

class Util {

    public static Sitemap toSitemap(Article article) {
        Sitemap sitemap = new Sitemap();
        sitemap.setChangefreq(Sitemap.CHANGEFREQ_WEEKLY);
        sitemap.setLoc(article.getUrl());
        sitemap.setLastmod(article.getModified());
        sitemap.setPriority(0.5f);
        return sitemap;
    }

    public static Sitemap toSitemap(ArticleCategory category) {
        Sitemap sitemap = new Sitemap();
        sitemap.setChangefreq(Sitemap.CHANGEFREQ_WEEKLY);
        sitemap.setLoc(category.getUrl());
        sitemap.setLastmod(category.getModified());
        sitemap.setPriority(0.8f);
        return sitemap;
    }
}
