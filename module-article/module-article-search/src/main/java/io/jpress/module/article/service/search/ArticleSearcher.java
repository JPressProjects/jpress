package io.jpress.module.article.service.search;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;


public interface ArticleSearcher {

    String HIGH_LIGHT_CLASS = "search-highlight";

    void addArticle(Article article);

    void deleteArticle(Object id);

    void updateArticle(Article article);

    Page<Article> search(String keyword, int pageNum, int pageSize);
}
