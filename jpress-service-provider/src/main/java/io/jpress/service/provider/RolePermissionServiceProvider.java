package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.RolePermissionService;
import io.jpress.model.RolePermission;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class RolePermissionServiceProvider extends JbootServiceBase<RolePermission> implements RolePermissionService {

}