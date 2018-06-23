package io.jpress.module.article.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticlePayRecordService;
import io.jpress.module.article.model.ArticlePayRecord;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ArticlePayRecordServiceProvider extends JbootServiceBase<ArticlePayRecord> implements ArticlePayRecordService {

}