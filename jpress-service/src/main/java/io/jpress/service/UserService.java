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
package io.jpress.service;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService extends JbootServiceJoiner {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    User findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 User
     */
    List<User> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    boolean deleteByIds(Object... ids);

    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(User model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return
     */
    Object save(User model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return 新增或更新成功后，返回该 Model 的主键值
     */
    Object saveOrUpdate(User model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(User model);

    Page<User> _paginate(int page, int pagesize, Columns columns, Long memberGroupId);


    User findByUsernameOrEmail(String usernameOrEmail);

    Ret doValidateUserPwd(User user, String pwd);

    int findCountByStatus(String status);

    boolean doChangeStatus(long id, String status);

    User findFirstByUsername(String username);

    User findFirstByEmail(String email);

    User findFirstByWxUnionid(String unioinId);

    User findFirstByWxOpenid(String openId);

    User findFirstByQQOpenid(String openId);

    User findFirstByWeiboOpenid(String openId);

    User findFirstByGithubOpenid(String openId);

    User findFirstByGiteeOpenid(String openId);

    User findFirstByDingdingOpenid(String openId);

    BigDecimal queryUserAmount(Object userId);

    boolean updateUserAmount(Object userId, BigDecimal oldAmount, BigDecimal updateAmount);

    List<User> findListByTagIds(Columns columns, Object... tagIds);


    /**
     * 根据mobile 查询 model
     * @param mobile
     * @return
     */
    User findFirstByMobile(String mobile);
}