package io.jpress.module.article.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.model.ArticleComment;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ArticleCommentServiceProvider extends JbootServiceBase<ArticleComment> implements ArticleCommentService {

}