package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserService;
import io.jpress.model.User;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class UserServiceProvider extends JbootServiceBase<User> implements UserService {

}