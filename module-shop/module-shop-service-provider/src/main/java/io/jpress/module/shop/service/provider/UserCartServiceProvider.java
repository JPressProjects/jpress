package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.UserCartService;
import io.jpress.module.shop.model.UserCart;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {

}