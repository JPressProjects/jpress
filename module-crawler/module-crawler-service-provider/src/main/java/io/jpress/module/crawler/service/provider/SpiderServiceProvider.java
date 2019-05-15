package io.jpress.module.crawler.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.crawler.service.SpiderService;
import io.jpress.module.crawler.model.Spider;
import io.jboot.service.JbootServiceBase;

@Bean
public class SpiderServiceProvider extends JbootServiceBase<Spider> implements SpiderService {

}