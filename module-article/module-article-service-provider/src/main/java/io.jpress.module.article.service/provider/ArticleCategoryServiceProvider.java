package io.jpress.module.article.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.model.ArticleCategory;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class ArticleCategoryServiceProvider extends JbootServiceBase<ArticleCategory> implements ArticleCategoryService {


    @Override
    public List<ArticleCategory> findListByType(String type) {
        return DAO.findListByColumn("type", type);
    }
}