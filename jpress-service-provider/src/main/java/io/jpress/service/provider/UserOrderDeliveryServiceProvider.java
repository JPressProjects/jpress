package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserOrderDeliveryService;
import io.jpress.model.UserOrderDelivery;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderDeliveryServiceProvider extends JbootServiceBase<UserOrderDelivery> implements UserOrderDeliveryService {

}