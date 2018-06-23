package io.jpress.module.page.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.module.page.model.SinglePage;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class SinglePageServiceProvider extends JbootServiceBase<SinglePage> implements SinglePageService {

}