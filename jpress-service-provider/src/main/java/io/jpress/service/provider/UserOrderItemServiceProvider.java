package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserOrderItemService;
import io.jpress.model.UserOrderItem;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderItemServiceProvider extends JbootServiceBase<UserOrderItem> implements UserOrderItemService {

}