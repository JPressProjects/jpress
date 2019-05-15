package io.jpress.module.crawler.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.crawler.service.KeywordService;
import io.jpress.module.crawler.model.Keyword;
import io.jboot.service.JbootServiceBase;

@Bean
public class KeywordServiceProvider extends JbootServiceBase<Keyword> implements KeywordService {

}