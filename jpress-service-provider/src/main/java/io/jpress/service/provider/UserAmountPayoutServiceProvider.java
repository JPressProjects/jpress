package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserAmountPayoutService;
import io.jpress.model.UserAmountPayout;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserAmountPayoutServiceProvider extends JbootServiceBase<UserAmountPayout> implements UserAmountPayoutService {

}