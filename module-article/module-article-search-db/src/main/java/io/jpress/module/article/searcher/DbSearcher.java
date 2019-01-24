package io.jpress.module.article.searcher;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.search.ArticleSearcher;


public class DbSearcher implements ArticleSearcher {

    @Override
    public void init() {

    }

    @Override
    public void addArticle(Article bean) {

    }

    @Override
    public void deleteArticle(Object id) {

    }

    @Override
    public void updateArticle(Article bean) {

    }

    @Override
    public Page<Article> search(String keyword) {
        return null;
    }

    @Override
    public Page<Article> search(String queryString, int pageNum, int pageSize) {
        return null;
    }
}
