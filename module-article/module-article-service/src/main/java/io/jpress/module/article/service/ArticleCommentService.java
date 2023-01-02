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
package io.jpress.module.article.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.article.model.ArticleComment;

import java.util.List;

public interface ArticleCommentService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    ArticleComment findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 ArticleComment
     */
    List<ArticleComment> findAll();


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
     * 批量修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    boolean batchChangeStatusByIds(String status, Object... ids);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(ArticleComment model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return
     */
    Object save(ArticleComment model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return 新增或更新成功后，返回该 Model 的主键值
     */
    Object saveOrUpdate(ArticleComment model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(ArticleComment model);

    Page<ArticleComment> paginate(int page, int pagesieze);

    List<ArticleComment> findListByColumns(Columns columns, String orderBy, Integer count);

    boolean doChangeStatus(long id, String status);

    long findCountByStatus(String status);

    long findCountByArticleId(Long articleId);

    Page<ArticleComment> _paginateByStatus(int page, int pagesize, Columns columns, String status);

    Page<ArticleComment> _paginateWithoutTrash(int page, int pagesize, Columns columns);

    Page<ArticleComment> _paginateByUserId(int page, int pagesize, long userId);

    Page<ArticleComment> paginateByArticleIdInNormal(int page, int pagesize, long articleId);

    void doIncCommentReplyCount(long commentId);

    boolean deleteByArticleId(Object articleId);


}