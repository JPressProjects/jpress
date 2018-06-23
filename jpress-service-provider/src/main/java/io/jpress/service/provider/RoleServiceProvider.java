package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jpress.service.RoleService;
import io.jpress.model.Role;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Bean
@Singleton
public class RoleServiceProvider extends JbootServiceBase<Role> implements RoleService {

    @Override
    public boolean isSupperAdmin(long userId) {
        List<Role> roles = findRoleListByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        for (Role role : roles) {
            if (role.isSuperAdmin()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean hasRole(long userId, String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        List<Role> roleList = findRoleListByUserId(userId);
        if (roleList == null || roleList.isEmpty()) {
            return false;
        }

        for (String roleFlag : roles) {
            boolean hasRole = false;
            for (Role role : roleList) {
                if (roleFlag.equals(role.getFlag())) {
                    hasRole = true;
                    break;
                }
            }

            if (!hasRole) {
                return false;
            }
        }

        return true;
    }


    @Override
    public boolean hasAnyRole(long userId, String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        List<Role> roleList = findRoleListByUserId(userId);
        if (roleList == null || roleList.isEmpty()) {
            return false;
        }

        for (String roleFlag : roles) {
            for (Role role : roleList) {
                if (roleFlag.equals(role.getFlag())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Cacheable(name = "role", key = "user_roles:#(userId)", nullCacheEnable = true)
    private List<Role> findRoleListByUserId(long userId) {
        String sql = "select * from user_role where user_id = ?";
        List<Record> records = Db.find(sql, userId);
        if (records == null || records.isEmpty()) {
            return null;
        }

        List<Role> roles = new ArrayList<>();
        for (Record record : records) {
            Role role = findById(record.getLong("role_id"));
            if (role != null) roles.add(role);
        }
        return roles;
    }

}