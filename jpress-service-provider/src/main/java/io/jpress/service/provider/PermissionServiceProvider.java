package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.*;
import io.jpress.service.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Bean
@Singleton
public class PermissionServiceProvider extends JbootServiceBase<Permission> implements PermissionService {

    @Inject
    private RoleService roleService;

    @Override
    public int sync(List<Permission> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return 0;
        }

        int syncCounter = 0;
        for (Permission permission : permissions) {

            Columns columns = Columns.create("node", permission.getNode());
            columns.eq("action_key", permission.getActionKey());

            Permission dbPermission = DAO.findFirstByColumns(columns);

            if (dbPermission == null) {
                permission.save();
                syncCounter++;
            }
        }
        return syncCounter;
    }


    @Override
    public boolean hasPermission(long userId, String actionKey) {
        Set<Permission> permissions = findPermissionListByUserId(userId);
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (Permission permission : permissions) {
            if (permission.getActionKey().equals(actionKey)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Page<Permission> page(int size, int count) {
        return DAO.paginate(size, count);
    }

    @Override
    public Page<Permission> page(int size, int count, int type) {
        return DAO.paginateByColumn(size, count, Column.create("type", type), "id desc");
    }


    @Cacheable(name = "permission", key = "user_permissions:#(userId)", nullCacheEnable = true)
    private Set<Permission> findPermissionListByUserId(long userId) {

        Set<Permission> permissions = new HashSet<>();
        String sql = "select * from user_role where user_id = ? ";
        List<Record> userRoleRecords = Db.find(sql, userId);
        if (userRoleRecords != null) {
            for (Record userRoleRecord : userRoleRecords) {
                List<Permission> rolePermissions = findPermissionListByRoleId(userRoleRecord.getLong("role_id"));
                if (rolePermissions != null) {
                    permissions.addAll(rolePermissions);
                }
            }
        }

        sql = "select * from user_permission where user_id = ?";
        List<Record> userPermissionList = Db.find(sql, userId);
        if (userPermissionList != null) {
            for (Record userPermission : userPermissionList) {
                if (userPermission.getInt("own") > 0) {
                    permissions.add(findById(userPermission.getLong("permission_id")));
                } else {
                    permissions.remove(findById(userPermission.getLong("permission_id")));
                }
            }
        }

        return permissions;
    }


    private List<Permission> findPermissionListByRoleId(long roleId) {
        String sql = "select * from role_permission where role_id = ? ";
        List<Record> rolePermissionRecords = Db.find(sql, roleId);
        if (rolePermissionRecords == null || rolePermissionRecords.isEmpty()) {
            return null;
        }

        List<Permission> permissionList = new ArrayList<>();
        for (Record rolePermissionRecord : rolePermissionRecords) {
            Permission permission = findById(rolePermissionRecord.getLong("permission_id"));
            if (permission != null) permissionList.add(permission);
        }

        return permissionList;
    }


}