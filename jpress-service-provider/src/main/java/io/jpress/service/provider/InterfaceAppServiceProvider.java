package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.InterfaceAppService;
import io.jpress.model.InterfaceApp;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class InterfaceAppServiceProvider extends JbootServiceBase<InterfaceApp> implements InterfaceAppService {

}