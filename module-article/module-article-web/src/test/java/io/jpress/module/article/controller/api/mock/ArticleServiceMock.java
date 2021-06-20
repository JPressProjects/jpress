package io.jpress.module.article.controller.api.mock;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.test.MockClass;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.provider.ArticleServiceProvider;

import java.util.ArrayList;
import java.util.List;

@MockClass
public class ArticleServiceMock extends ArticleServiceProvider implements ArticleService {

    @Override
    public Article findById(Object id) {
        Article article = new Article();
        article.setId((Long) id);
        article.setStatus(Article.STATUS_NORMAL);
        return article;
    }

    @Override
    public Page<Article> paginateInNormal(int page, int pagesize) {

        Article article = new Article();
        article.setId(1L);
        article.setStatus(Article.STATUS_NORMAL);

        List<Article> list = new ArrayList<>();
        list.add(article);

        return new Page<>(list,1,1,1,1);
    }


    @Override
    public Page<Article> paginateInNormal(int page, int pagesize, String orderBy) {
        Article article = new Article();
        article.setId(1L);
        article.setStatus(Article.STATUS_NORMAL);

        List<Article> list = new ArrayList<>();
        list.add(article);

        return new Page<>(list,1,1,1,1);
    }
}
