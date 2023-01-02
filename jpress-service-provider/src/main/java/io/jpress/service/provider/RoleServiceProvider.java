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
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.model.Permission;
import io.jpress.model.Role;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class RoleServiceProvider extends JPressServiceBase<Role> implements RoleService {

    @Inject
    private PermissionService permissionService;


    @Override
    @CacheEvict(name = "user_role", key = "*")
    public boolean deleteById(Object id) {
        return Db.tx(() -> {
            Db.update("delete from user_role_mapping where role_id = ? ", id);
            Db.update("delete from role_permission_mapping where role_id = ? ", id);
            return RoleServiceProvider.super.deleteById(id);
        });
    }

    @Override
    @CacheEvict(name = "user_role", key = "*")
    public boolean deleteByIds(Object... ids) {
        return Db.tx(() -> {
            Db.update("delete from user_role_mapping where role_id in  " + SqlUtils.buildInSqlPara(ids));
            Db.update("delete from role_permission_mapping where role_id in  " + SqlUtils.buildInSqlPara(ids));
            return Db.update("delete from role where id in " + SqlUtils.buildInSqlPara(ids)) > 0;
        });
    }

    @Override
    @CacheEvict(name = "user_role", key = "*")
    public boolean update(Role model) {
        return super.update(model);
    }

    @Override
    @CacheEvict(name = "user_role", key = "*")
    public Object saveOrUpdate(Role model) {
        return super.saveOrUpdate(model);
    }

    @Override
    public boolean isSupperAdmin(long userId) {
        List<Role> roles = findListByUserId(userId);
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

        List<Role> roleList = findListByUserId(userId);
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

        List<Role> roleList = findListByUserId(userId);
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

        List<Role> roleList = findListByUserId(userId);
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
    public boolean hasAnyRole(long userId) {
        List<Record> records = findAllUserRoleMapping();
        if (records == null || records.isEmpty()) {
            return false;
        }

        for (Record record : records){
            Long uid = record.getLong("user_id");
            if (uid != null && uid.equals(userId)){
                return true;
            }
        }

        return false;
    }

    @Override
    @CacheEvict(name = "user_permission", key = "*")
    public boolean addPermission(long roleId, long permissionId) {
        Record rolePermission = new Record().set("role_id", roleId).set("permission_id", permissionId);
        return Db.save("role_permission_mapping", rolePermission);
    }

    @Override
    @CacheEvict(name = "user_permission", key = "*")
    public boolean delPermission(long roleId, long permissionId) {
        Db.delete("delete from role_permission_mapping where role_id=? and permission_id=?", roleId, permissionId);
        return true;
    }

    @Override
    public boolean hasPermission(long roleId, long permissionId) {

        List<Permission> permissions = permissionService.findPermissionListByRoleId(roleId);

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
    @CachesEvict({
            @CacheEvict(name = "user_role", key = "*"),
            @CacheEvict(name = "user_permission", key = "*")
    })
    public boolean doResetUserRoles(long userId, Long... RoleIds) {
        if (RoleIds == null || RoleIds.length == 0) {
            return Db.delete("delete from user_role_mapping where user_id = ? ", userId) > 0;
        }

        return Db.tx(() -> {
            Db.delete("delete from user_role_mapping where user_id = ? ", userId);

            List<Record> records = new ArrayList<>();
            for (Long roleId : RoleIds) {
                Record record = new Record();
                record.set("user_id", userId);
                record.set("role_id", roleId);
                records.add(record);
            }

            Db.batchSave("user_role_mapping", records, records.size());

            return true;
        });
    }


    @Override
    @Cacheable(name = "user_role", key = "user_roles:#(userId)", nullCacheEnable = true)
    public List<Role> findListByUserId(long userId) {
        List<Record> records = findAllUserRoleMapping();
        if (records == null || records.isEmpty()) {
            return null;
        }

        records = records.stream()
                .filter(record -> {
                    Long uid = record.getLong("user_id");
                    return uid != null && uid.equals(userId);
                })
                .collect(Collectors.toList());

        List<Role> roles = new ArrayList<>();
        for (Record record : records) {
            Role role = findById(record.getLong("role_id"));
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }


    @Cacheable(name = "user_role", key = "all", nullCacheEnable = true)
    public List<Record> findAllUserRoleMapping() {
        return Db.findAll("user_role_mapping");
    }


    @Override
    public boolean doChangeRoleByIds(Long roleId, Object... ids) {

        for (Object id : ids) {
            //删除role缓存
            Jboot.getCache().remove("user_role", "user_roles:" + id);
        }

        return Db.tx(() -> {

            //清空用户的其他所有角色
            for (Object id : ids) {
                Db.delete("delete from user_role_mapping where user_id = ? ", id);
            }

            //添加新的映射
            List<Record> records = new ArrayList<>();
            for (Object id : ids) {
                Record record = new Record();
                record.set("user_id", id);
                record.set("role_id", roleId);
                records.add(record);
            }

            Db.batchSave("user_role_mapping", records, records.size());

            return true;
        });
    }



}