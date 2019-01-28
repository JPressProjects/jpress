package io.jpress.module.article.service;


import com.jfinal.aop.Aop;
import io.jpress.module.article.searcher.DbSearcher;
import io.jpress.module.article.service.search.ArticleSearcher;

public class ArticleSearcherFactory {

    public static ArticleSearcher getSearcher() {
        return Aop.get(DbSearcher.class);
    }
}
