package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.UserOrderService;
import io.jpress.module.shop.model.UserOrder;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderServiceProvider extends JbootServiceBase<UserOrder> implements UserOrderService {

}