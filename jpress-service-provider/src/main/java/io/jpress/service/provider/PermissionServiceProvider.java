package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Permission;
import io.jpress.model.RolePermission;
import io.jpress.model.UserPermission;
import io.jpress.model.UserRole;
import io.jpress.service.PermissionService;
import io.jpress.service.RolePermissionService;
import io.jpress.service.UserPermissionService;
import io.jpress.service.UserRoleService;

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
    private UserRoleService userRoleService;
    @Inject
    private UserPermissionService userPermissionService;
    @Inject
    private RolePermissionService rolePermissionService;

    @Override
    public int sync(List<Permission> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return 0;
        }

        int syncCounter = 0;
        for (Permission permission : permissions) {

            Columns columns = Columns.create("controller", permission.getController());
            columns.eq("actionKey", permission.getActionKey());

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
    public boolean isSupperAdmin(long userId) {
        List<UserRole> userRoles = userRoleService.findListByUserId(userId);
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        for (UserRole userRole : userRoles) {
            if (userRole.isSuperRole()) return true;
        }

        return false;
    }

    private Set<Permission> findPermissionListByUserId(long userId) {

        Set<Permission> permissions = new HashSet<>();
        List<UserRole> userRoles = userRoleService.findListByUserId(userId);
        if (userRoles != null) {
            for (UserRole userRole : userRoles) {
                List<Permission> rolePermissions = findPermissionListByRoleId(userRole.getRoleId());
                if (rolePermissions != null) {
                    permissions.addAll(rolePermissions);
                }
            }
        }

        List<UserPermission> userPermissionList = userPermissionService.findListByUserId(userId);
        if (userPermissionList != null) {
            for (UserPermission userPermission : userPermissionList) {
                if (userPermission.isOwn()) {
                    permissions.add(findById(userPermission.getPermissionId()));
                } else {
                    permissions.remove(findById(userPermission.getPermissionId()));
                }
            }
        }

        return permissions;
    }


    private List<Permission> findPermissionListByRoleId(long roleId) {
        List<RolePermission> rolePermissionList = rolePermissionService.findListByRoleId(roleId);
        if (rolePermissionList == null || rolePermissionList.isEmpty()) {
            return null;
        }

        List<Permission> permissionList = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissionList) {
            Permission permission = findById(rolePermission.getPermissionId());
            if (permission != null) permissionList.add(permission);
        }

        return permissionList;
    }


}