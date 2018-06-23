package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UtmService;
import io.jpress.model.Utm;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class UtmServiceProvider extends JbootServiceBase<Utm> implements UtmService {

}