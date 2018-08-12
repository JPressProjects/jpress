package io.jpress.web.admin.kits;

import io.jpress.model.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.admin.kits
 */
public class PermissionKits {

    public static Map<String, List<Permission>> groupPermission(List<Permission> permissions) {

        Map<String, List<Permission>> map = new HashMap<>();

        for (Permission permission : permissions) {
            List<Permission> permissionList = map.get(permission.getShortNode());
            if (permissionList == null) {
                permissionList = new ArrayList<>();
                map.put(permission.getShortNode(), permissionList);
            }
            permissionList.add(permission);
        }

        return map;
    }


}
