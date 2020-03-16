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
package io.jpress.module.article.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.article.model.ArticleComment;

import java.util.List;

public interface ArticleCommentService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ArticleComment findById(Object id);


    /**
     * find all model
     *
     * @return all <ArticleComment
     */
    public List<ArticleComment> findAll();


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
     * 批量修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    public boolean batchChangeStatusByIds(String status, Object... ids);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(ArticleComment model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(ArticleComment model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(ArticleComment model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ArticleComment model);

    public Page<ArticleComment> paginate(int page, int pagesieze);

    public List<ArticleComment> findListByColumns(Columns columns, String orderBy, Integer count);

    public boolean doChangeStatus(long id, String status);

    public long findCountByStatus(String status);

    public long findCountByArticleId(Long articleId);

    public Page<ArticleComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status);

    public Page<ArticleComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword);

    public Page<ArticleComment> _paginateByUserId(int page, int pagesize, long userId);

    public Page<ArticleComment> paginateByArticleIdInNormal(int page, int pagesize, long articleId);

    public void doIncCommentReplyCount(long commentId);

    public boolean deleteByArticleId(Object articleId);


}