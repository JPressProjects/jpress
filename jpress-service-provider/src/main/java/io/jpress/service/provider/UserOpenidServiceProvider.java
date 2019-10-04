package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserOpenidService;
import io.jpress.model.UserOpenid;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOpenidServiceProvider extends JbootServiceBase<UserOpenid> implements UserOpenidService {

}