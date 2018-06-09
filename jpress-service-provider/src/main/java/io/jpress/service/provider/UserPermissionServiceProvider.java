package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserPermissionService;
import io.jpress.model.UserPermission;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class UserPermissionServiceProvider extends JbootServiceBase<UserPermission> implements UserPermissionService {

    @Override
    public List<UserPermission> findListByUserId(long userId) {
        return DAO.findListByColumn("user_id", userId);
    }
}