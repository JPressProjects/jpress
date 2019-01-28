package io.jpress.module.article.service;


import com.jfinal.aop.Aop;
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.module.article.searcher.DbSearcher;
import io.jpress.module.article.searcher.LuceneSearcher;
import io.jpress.module.article.service.search.ArticleSearcher;

public class ArticleSearcherFactory {

    public static ArticleSearcher getSearcher() {

        String engine = JPressOptions.get("article_search_engine");

        if (StrUtil.isBlank(engine)) {
            return Aop.get(DbSearcher.class);
        }

        switch (engine) {
            case "sql":
                return Aop.get(DbSearcher.class);
            case "lucene":
                return Aop.get(LuceneSearcher.class);
            case "es":
                return Aop.get(LuceneSearcher.class);
            case "aliopensearch":
                return Aop.get(LuceneSearcher.class);
        }

        ArticleSearcher searcher = JbootSpiLoader.load(ArticleSearcher.class, engine);
        return searcher != null ? searcher : Aop.get(DbSearcher.class);

    }
}
