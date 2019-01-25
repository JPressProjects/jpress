package io.jpress.module.article.search;


import com.jfinal.aop.Aop;
import io.jpress.module.article.searcher.DbSearcher;

public class ArticleSearcherFactory {

    public static ArticleSearcher getSearcher() {
        return Aop.get(DbSearcher.class);
    }
}
