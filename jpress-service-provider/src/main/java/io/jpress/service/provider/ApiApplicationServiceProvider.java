package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.ApiApplicationService;
import io.jpress.model.ApiApplication;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ApiApplicationServiceProvider extends JbootServiceBase<ApiApplication> implements ApiApplicationService {

}