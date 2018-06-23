package io.jpress.module.article.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.model.Article;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ArticleServiceProvider extends JbootServiceBase<Article> implements ArticleService {

}