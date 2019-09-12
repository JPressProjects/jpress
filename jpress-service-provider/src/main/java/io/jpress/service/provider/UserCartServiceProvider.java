package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserCartService;
import io.jpress.model.UserCart;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {

}