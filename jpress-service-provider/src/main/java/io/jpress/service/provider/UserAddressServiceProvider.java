package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserAddressService;
import io.jpress.model.UserAddress;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserAddressServiceProvider extends JbootServiceBase<UserAddress> implements UserAddressService {

}