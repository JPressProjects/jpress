package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.WechatMenuService;
import io.jpress.model.WechatMenu;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class WechatMenuServiceProvider extends JbootServiceBase<WechatMenu> implements WechatMenuService {

}