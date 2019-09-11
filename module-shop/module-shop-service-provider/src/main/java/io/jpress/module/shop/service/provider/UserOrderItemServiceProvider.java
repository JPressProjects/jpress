package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.UserOrderItemService;
import io.jpress.module.shop.model.UserOrderItem;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderItemServiceProvider extends JbootServiceBase<UserOrderItem> implements UserOrderItemService {

}