package io.jpress.web.sharekit;

import io.jboot.Jboot;
import io.jpress.model.User;
import io.jpress.service.RoleService;
import io.jpress.web.base.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */

public class PermissionKits {

    public static boolean hasPermission(long roleId, long permissionId) {
        RoleService service = Jboot.bean(RoleService.class);
        return service.hasPermission(roleId, permissionId);
    }


    public static final boolean hasRole(long userId, long roleId) {
        RoleService roleService = Jboot.bean(RoleService.class);
        return roleService.hasRole(userId, roleId);
    }

    public static final boolean hasRole(long roleId) {
        User user = UserInterceptor.getThreadLocalUser();
        RoleService roleService = Jboot.bean(RoleService.class);
        return roleService.hasRole(user.getId(), roleId);
    }

    public static final boolean hasRole(String roleFlag) {
        User user = UserInterceptor.getThreadLocalUser();
        RoleService roleService = Jboot.bean(RoleService.class);
        return roleService.hasRole(user.getId(), roleFlag);
    }

    public static final boolean hasRole(long userId, String roleFlag) {
        RoleService roleService = Jboot.bean(RoleService.class);
        return roleService.hasRole(userId, roleFlag);
    }
}
