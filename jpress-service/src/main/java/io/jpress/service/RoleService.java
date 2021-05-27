/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.service;

import com.jfinal.plugin.activerecord.Record;
import io.jpress.model.Role;

import java.util.List;

public interface RoleService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Role findById(Object id);


    /**
     * find all model
     *
     * @return all <Role
     */
    public List<Role> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * 删除多个id
     *
     * @param ids
     * @return
     */
    public boolean deleteByIds(Object... ids);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Role model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(Role model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(Role model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Role model);

    public boolean isSupperAdmin(long userId);

    public boolean hasRole(long userId, String... roles);

    public boolean hasRole(long userId, long... roles);

    public boolean hasAnyRole(long userId, String... roles);

    public boolean hasAnyRole(long userId);

    public boolean addPermission(long roleId, long permissionId);

    public boolean delPermission(long roleId, long permissionId);

    public boolean hasPermission(long roleId, long permissionId);

    public boolean doResetUserRoles(long userId, Long... RoleIds);

    public boolean doChangeRoleByIds(Long roleId, Object... ids);

    public List<Role> findRoleListByUserId(long userId);

    public List<Record> findAllUserRoleMapping();

}