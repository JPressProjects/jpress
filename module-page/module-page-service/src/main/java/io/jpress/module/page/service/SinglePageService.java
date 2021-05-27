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
package io.jpress.module.page.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.page.model.SinglePage;

import java.util.List;

public interface SinglePageService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public SinglePage findById(Object id);


    /**
     * find all model
     *
     * @return all <SinglePage
     */
    public List<SinglePage> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    public void deleteCacheById(Object id);


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
    public boolean delete(SinglePage model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(SinglePage model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(SinglePage model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(SinglePage model);


    public Page<SinglePage> _paginateByStatus(int page, int pagesize, String title, String status);


    public Page<SinglePage> _paginateWithoutTrash(int page, int pagesize, String title);

    public boolean doChangeStatus(long id, String status);

    public int findCountByStatus(String status);

    public SinglePage findFirstBySlug(String slug);

    public List<SinglePage> findListByFlag(String flag);

    public void doIncViewCount(long id);

}