package io.jpress.module.article.sitemap;

import com.jfinal.aop.Inject;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class ArticleCategorySitemapProvider implements SitemapProvider {

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public String getName() {
        return "article_categories";
    }

    @Override
    public Date getLastmod() {
        List<Sitemap> sitemaps = getSitemaps();
        return sitemaps == null || sitemaps.isEmpty() ? null : sitemaps.get(0).getLastmod();
    }


    @Override
    public List<Sitemap> getSitemaps() {
        List<ArticleCategory> tagList = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        if (tagList == null || tagList.isEmpty()) {
            return null;
        }
        return tagList.stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}
