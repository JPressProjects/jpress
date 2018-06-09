package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.RoleService;
import io.jpress.model.Role;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class RoleServiceProvider extends JbootServiceBase<Role> implements RoleService {

}