package io.jpress.addon.articlemeta.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.addon.articlemeta.service.ArticleMetaInfoService;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;
import io.jboot.service.JbootServiceBase;

@Bean
public class ArticleMetaInfoServiceProvider extends JbootServiceBase<ArticleMetaInfo> implements ArticleMetaInfoService {

}