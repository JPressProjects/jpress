package io.jpress.module.crawler.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.crawler.service.WebpageService;
import io.jpress.module.crawler.model.Webpage;
import io.jboot.service.JbootServiceBase;

@Bean
public class WebpageServiceProvider extends JbootServiceBase<Webpage> implements WebpageService {

}