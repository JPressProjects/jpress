package io.jpress.module.article.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.model.ArticleCategoryMapping;
import io.jpress.module.article.service.ArticleCategoryMappingService;

import javax.inject.Singleton;

@Bean
@Singleton
public class ArticleCategoryMappingServiceProvider extends JbootServiceBase<ArticleCategoryMapping> implements ArticleCategoryMappingService {

}