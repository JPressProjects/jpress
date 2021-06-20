package io.jpress.module.article.controller.api.mock;

import io.jboot.test.MockClass;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.provider.ArticleCategoryServiceProvider;

import java.util.ArrayList;
import java.util.List;

@MockClass
public class ArticleCategoryServiceMock extends ArticleCategoryServiceProvider implements ArticleCategoryService {

    @Override
    public ArticleCategory findById(Object id) {
        ArticleCategory cat1 = new ArticleCategory();
        cat1.setTitle("分类1");
        cat1.setPid(0L);
        cat1.setId(1L);
        return cat1;
    }


    @Override
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

}
