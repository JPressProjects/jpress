package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserOrderService;
import io.jpress.model.UserOrder;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderServiceProvider extends JbootServiceBase<UserOrder> implements UserOrderService {

}