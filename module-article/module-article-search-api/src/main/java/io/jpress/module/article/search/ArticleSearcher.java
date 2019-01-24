package io.jpress.module.article.search;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;


public interface ArticleSearcher {

    public void init();

    public void addArticle(Article bean);

    public void deleteArticle(Object id);

    public void updateArticle(Article bean);

    public Page<Article> search(String keyword);

    public Page<Article> search(String queryString, int pageNum, int pageSize);
}
