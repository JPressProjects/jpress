package io.jpress.module.crawler.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.crawler.service.KeywordCategoryService;
import io.jpress.module.crawler.model.KeywordCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class KeywordCategoryServiceProvider extends JbootServiceBase<KeywordCategory> implements KeywordCategoryService {

}