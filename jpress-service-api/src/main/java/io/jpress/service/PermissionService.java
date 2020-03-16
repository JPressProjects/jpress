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

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.Permission;

import java.util.List;

public interface PermissionService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Permission findById(Object id);


    /**
     * find all model
     *
     * @return all <Permission
     */
    public List<Permission> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Permission model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(Permission model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(Permission model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Permission model);


    public int sync(List<Permission> permissions);

    public boolean hasPermission(long userId, String actionKey);

    public boolean hasPermission(long userId, long permissionId);

    public Page<Permission> page(int size, int count);

    public Page<Permission> page(int size, int count, int type);

    public List<Permission> findListByType(String type);

    public List<Permission> findPermissionListByUserId(long userId);

    public List<Permission> findPermissionListByRoleId(long roleId);

    public List<Permission> findListByNode(String node);

}