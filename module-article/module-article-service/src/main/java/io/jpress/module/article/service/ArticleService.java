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
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.article.model.Article;

import java.util.List;

public interface ArticleService extends JbootServiceJoiner {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    Article findById(Object id);

    Article findByTitle(String title);


    /**
     * 查询所有的数据
     *
     * @return 所有的 Article
     */
    List<Article> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    void removeCacheById(Object id);


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
    boolean delete(Article model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return
     */
    Object save(Article model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return 新增或更新成功后，返回该 Model 的主键值
     */
    Object saveOrUpdate(Article model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(Article model);


    void doUpdateCategorys(long articleId, Long[] categoryIds);

    void doUpdateCommentCount(long articleId);

    boolean doChangeStatus(long id, String status);

    Long findCountByStatus(String status);

    Article findFirstBySlug(String slug);

    Article findNextById(long id);

    Article findPreviousById(long id);

    List<Article> findListByColumns(Columns columns, String orderBy, Integer count);

    List<Article> findListByCategoryId(long categoryId, boolean includeChildren, Columns columns, String orderBy, Integer count);

    List<Article> findRelevantListByArticleId(long ArticleId, String status, Integer count);

    Page<Article> _paginateByColumns(int page, int pagesize, Columns baseColumns, String orderBy);

    Page<Article> _paginateByUserId(int page, int pagesize, Long userId);

    Page<Article> paginateInNormal(int page, int pagesize);

    Page<Article> paginateInNormal(int page, int pagesize, String orderBy);

    Page<Article> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, boolean includeChildren, String orderBy);

    void doIncArticleViewCount(long articleId);

    void doIncArticleCommentCount(long articleId);

    Page<Article> search(String queryString, int pageNum, int pageSize);

    Page<Article> searchIndb(String queryString, int pageNum, int pageSize);


}