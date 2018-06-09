package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.OptionService;
import io.jpress.model.Option;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class OptionServiceProvider extends JbootServiceBase<Option> implements OptionService {

}