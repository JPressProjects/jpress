package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserRoleService;
import io.jpress.model.UserRole;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class UserRoleServiceProvider extends JbootServiceBase<UserRole> implements UserRoleService {

    @Override
    public List<UserRole> findListByUserId(long userId) {
        return DAO.findListByColumn("user_id", userId);
    }
}