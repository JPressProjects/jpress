package io.jpress.module.article.controller.api;

import org.junit.Test;

public class ArticleApiControllerTest extends BaseApiControllerTest {

    @Test
    public void detail() {
        mvc.get("/api/article/detail?id=1").printResult();
    }


    @Test
    public void categories() {
        mvc.get("/api/article/categories?type=tag").printResult();
    }



    @Test
    public void category() {
        mvc.get("/api/article/category?id=1").printResult();
    }

    @Test
    public void paginate() {
        mvc.get("/api/article/paginate").printResult();
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