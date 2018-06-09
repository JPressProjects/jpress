package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.RolePermissionService;
import io.jpress.model.RolePermission;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class RolePermissionServiceProvider extends JbootServiceBase<RolePermission> implements RolePermissionService {

    @Override
    public List<RolePermission> findListByRoleId(long roleId) {
        return DAO.findListByColumn("role_id", roleId);
    }
}