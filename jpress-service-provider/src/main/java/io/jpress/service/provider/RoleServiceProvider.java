package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Role;
import io.jpress.service.RoleService;

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
    public boolean hasRole(long userId, long... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        List<Role> roleList = findRoleListByUserId(userId);
        if (roleList == null || roleList.isEmpty()) {
            return false;
        }

        for (Long roleId : roles) {
            boolean hasRole = false;
            for (Role role : roleList) {
                if (roleId.equals(role.getId())) {
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

    @Override
    public boolean addPermission(long roleId, long permissionId) {
        Record rolePermission = new Record().set("role_id", roleId).set("permission_id", permissionId);
        Db.save("role_permission", rolePermission);
        return true;
    }

    @Override
    public boolean delPermission(long roleId, long permissionId) {
        Db.delete("delete from role_permission where role_id=? and permission_id=?", roleId, permissionId);
        return true;
    }

    @Override
    public boolean hasPermission(long roleId, long permissionId) {
        return Db.queryFirst("select * from role_permission where role_id = ? and permission_id = ?", roleId, permissionId) != null;
    }

    @Override
    public boolean doResetUserRoles(long userId, Long... RoleIds) {

        if (RoleIds == null || RoleIds.length == 0) {
            return Db.delete("delete from user_role where user_id = ? ", userId) > 0;
        }

        return Db.tx(() -> {
            Db.delete("delete from user_role where user_id = ? ", userId);

            List<Record> records = new ArrayList<>();
            for (Long roleId : RoleIds) {
                Record record = new Record();
                record.set("user_id", userId);
                record.set("role_id", roleId);
                records.add(record);
            }

            Db.batchSave("user_role", records, records.size());

            return true;
        });
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