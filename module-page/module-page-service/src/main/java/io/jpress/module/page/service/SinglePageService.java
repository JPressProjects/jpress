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
package io.jpress.module.page.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.page.model.SinglePage;

import java.util.List;

public interface SinglePageService extends JbootServiceJoiner {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    SinglePage findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 SinglePage
     */
    List<SinglePage> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    void deleteCacheById(Object id);


    /**
     * 删除多个id
     *
     * @param ids
     * @return
     */
    boolean deleteByIds(Object... ids);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(SinglePage model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return
     */
    Object save(SinglePage model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return 新增或更新成功后，返回该 Model 的主键值
     */
    Object saveOrUpdate(SinglePage model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(SinglePage model);


    Page<SinglePage> _paginateByStatus(int page, int pagesize, String title, String status);


    Page<SinglePage> _paginateWithoutTrash(int page, int pagesize, String title);

    boolean doChangeStatus(long id, String status);

    int findCountByStatus(String status);

    SinglePage findFirstBySlug(String slug);

    List<SinglePage> findListByFlag(String flag);

    List<SinglePage> findListByCategoryId(long categoryId);

    void doIncViewCount(long id);

    Page<SinglePage> paginateInNormal(int page, int pageSize, String orderBy);

    Page<SinglePage> paginateByCategoryIdInNormal(int page, int pageSize, Long categoryId, String orderBy);

    /**
     * 根据 columns 查询
     * @param page
     * @param pageSize
     * @param columns
     * @return
     */
    Page<SinglePage> _paginateWithoutTrashAndColumns(int page, int pageSize, Columns columns);

    /**
     * 根据 col 查询
     * @param page
     * @param pageSize
     * @param col
     * @return
     */
    Page<SinglePage> _paginateByColumns(int page, int pageSize, Columns col);


     List<SinglePage> findListByColumns(Columns columns, String orderBy, Integer count);
}