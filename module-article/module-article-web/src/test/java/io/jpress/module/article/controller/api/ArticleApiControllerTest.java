package io.jpress.module.article.controller.api;

import io.jboot.test.MockMethod;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.provider.ArticleCategoryServiceProvider;
import io.jpress.module.article.service.provider.ArticleServiceProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ArticleApiControllerTest extends BaseApiControllerTest {

    @Test
    public void detail() {
        mvc.get("/api/article/detail?id=1").printResult();
    }

    @MockMethod(targetClass = ArticleServiceProvider.class,targetMethod = "findById")
    public Article mock_findById(Object id){
        Article article = new Article();
        article.setId((Long) id);
        article.setStatus(Article.STATUS_NORMAL);
        return article;
    }

    @Test
    public void categories() {
        mvc.get("/api/article/categories?type=tag").printResult();
    }

    @MockMethod(targetClass = ArticleCategoryServiceProvider.class)
    public List<ArticleCategory> findListByTypeWithCache(String type) {
        List<ArticleCategory> articleCategories = new ArrayList<>();

        ArticleCategory cat1 = new ArticleCategory();
        cat1.setTitle("分类1");
        cat1.setPid(0L);
        cat1.setId(1L);


        ArticleCategory cat2 = new ArticleCategory();
        cat2.setTitle("分类2");
        cat2.setPid(0L);
        cat1.setId(2L);

        articleCategories.add(cat1);
        articleCategories.add(cat2);

        return articleCategories;
    }

    //ArticleCategoryServiceProvider.findListByTypeWithCache

    @Test
    public void category() {
    }

    @Test
    public void paginate() {
    }

    @Test
    public void tagArticles() {
    }

    @Test
    public void list() {
    }

    @Test
    public void relevantList() {
    }

    @Test
    public void create() {
    }

    @Test
    public void update() {
    }

    @Test
    public void commentPaginate() {
    }

    @Test
    public void postComment() {
    }

    @Test
    public void articleSearch() {
    }
}