/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.User;

import java.util.List;

public interface UserService extends JbootServiceJoiner{

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public User findById(Object id);


    /**
     * find all model
     *
     * @return all <User
     */
    public List<User> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    public boolean deleteByIds(Object... ids);

    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(User model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(User model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(User model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(User model);

    public Page<User> _paginate(int page, int pagesize, String username, String email, String status);

    public User findByUsernameOrEmail(String usernameOrEmail);

    public Ret doValidateUserPwd(User user, String pwd);

    public int findCountByStatus(String status);

    public boolean doChangeStatus(long id, String status);

    public User findFistByUsername(String username);

    public User findFistByEmail(String email);

    public User findFistByWxUnionid(String unioinId);

    public User findFistByWxOpenid(String openId);

    public User findFistByQQOpenid(String openId);

    public Long saveAndGetId(User user);
}