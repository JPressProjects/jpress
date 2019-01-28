package io.jpress.module.article.searcher;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.search.ArticleSearcher;


public class DbSearcher implements ArticleSearcher {


    @Inject
    private ArticleService articleService;

    @Override
    public void init() {
        // do noting
    }

    @Override
    public void addArticle(Article bean) {
        // do noting
    }

    @Override
    public void deleteArticle(Object id) {
        // do noting
    }

    @Override
    public void updateArticle(Article bean) {
        // do noting
    }

    @Override
    public Page<Article> search(String keyword) {
        return search(keyword, 1, 10);
    }

    @Override
    public Page<Article> search(String queryString, int pageNum, int pageSize) {
        return articleService.searchIndb(queryString, pageNum, pageSize);
    }
}
