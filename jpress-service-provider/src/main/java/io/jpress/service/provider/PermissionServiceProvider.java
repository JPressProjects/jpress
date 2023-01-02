/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.Permission;
import io.jpress.model.User;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Bean
public class PermissionServiceProvider extends JPressServiceBase<Permission> implements PermissionService {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;


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
        User user = userService.findById(userId);
        if (user == null || !user.isStatusOk()) {
            return false;
        }

        if (roleService.isSupperAdmin(userId)) {
            return true;
        }

        List<Permission> permissions = findPermissionListByUserId(userId);
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        String shortActionKey = actionKey.endsWith("/index")
                ? actionKey.substring(0,actionKey.length() - 6)
                : null;

        for (Permission permission : permissions) {
            if (permission.getActionKey().equals(actionKey)) {
                return true;
            }else if (shortActionKey != null && permission.getActionKey().equals(shortActionKey)){
                return true;
            }
        }


        return false;
    }


    @Override
    public boolean hasPermission(long userId, long permissionId) {
        User user = userService.findById(userId);
        if (user == null || !user.isStatusOk()) {
            return false;
        }

        if (roleService.isSupperAdmin(userId)) {
            return true;
        }

        List<Permission> permissions = findPermissionListByUserId(userId);
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (Permission permission : permissions) {
            if (permission.getId().equals(permissionId)) {
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

    @Override
    public List<Permission> findListByType(String type) {
        return DAO.findListByColumn("type", type);
    }


    @Override
    @Cacheable(name = "user_permission", key = "user_permissions:#(userId)", nullCacheEnable = true)
    public List<Permission> findPermissionListByUserId(long userId) {

        Set<Permission> permissions = new HashSet<>();
        String sql = "select * from user_role_mapping where user_id = ? ";
        List<Record> userRoleRecords = Db.find(sql, userId);
        if (userRoleRecords != null) {
            for (Record userRoleRecord : userRoleRecords) {
                List<Permission> rolePermissions = findPermissionListByRoleId(userRoleRecord.getLong("role_id"));
                if (rolePermissions != null) {
                    permissions.addAll(rolePermissions);
                }
            }
        }

        return new ArrayList<>(permissions);
    }


    @Override
    @Cacheable(name = "user_permission", key = "role:#(roleId)", nullCacheEnable = true)
    public List<Permission> findPermissionListByRoleId(long roleId) {
        String sql = "select * from role_permission_mapping where role_id = ? ";
        List<Record> rolePermissionRecords = Db.find(sql, roleId);
        if (rolePermissionRecords == null || rolePermissionRecords.isEmpty()) {
            return null;
        }

        List<Permission> permissionList = new ArrayList<>();
        for (Record rolePermissionRecord : rolePermissionRecords) {
            Permission permission = findById(rolePermissionRecord.getLong("permission_id"));
            if (permission != null) {
                permissionList.add(permission);
            }
        }

        return permissionList;
    }


    @Override
    public List<Permission> findListByNode(String node) {
        Columns columns = Columns.create();
        columns.likeAppendPercent("node", node);
        return DAO.findListByColumns(columns);
    }
}