package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MenuService;
import io.jpress.model.Menu;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class MenuServiceProvider extends JbootServiceBase<Menu> implements MenuService {

}