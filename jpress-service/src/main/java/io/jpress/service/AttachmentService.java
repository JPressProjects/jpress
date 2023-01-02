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

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.Attachment;

import java.util.List;

public interface AttachmentService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    Attachment findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 Attachment
     */
    List<Attachment> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(Attachment model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return
     */
    Object save(Attachment model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return 新增或更新成功后，返回该 Model 的主键值
     */
    Object saveOrUpdate(Attachment model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(Attachment model);


    Page<Attachment> _paginate(int page, int pagesieze, String title);

    /**
     * 根据 columns 查询
     * @param page
     * @param pageSize
     * @param columns
     * @return
     */
    Page<Attachment> _paginateByColumns(int page, int pageSize, Columns columns);

    boolean deleteByIds(Object... ids);
}