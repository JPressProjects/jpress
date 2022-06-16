package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.RolePermissionMappingService;
import io.jpress.model.RolePermissionMapping;
import io.jboot.service.JbootServiceBase;

@Bean
public class RolePermissionMappingServiceProvider extends JbootServiceBase<RolePermissionMapping> implements RolePermissionMappingService {

}