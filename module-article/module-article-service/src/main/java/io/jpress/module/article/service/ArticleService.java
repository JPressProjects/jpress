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
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.article.model.Article;

import java.util.List;

public interface ArticleService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Article findById(Object id);

    public Article findByTitle(String title);


    /**
     * find all model
     *
     * @return all <Article
     */
    public List<Article> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    public void removeCacheById(Object id);


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
    public boolean delete(Article model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(Article model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(Article model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Article model);


    public void doUpdateCategorys(long articleId, Long[] categoryIds);

    public void doUpdateCommentCount(long articleId);

    public boolean doChangeStatus(long id, String status);

    public Long findCountByStatus(String status);

    public Article findFirstBySlug(String slug);

    public Article findNextById(long id);

    public Article findPreviousById(long id);

    public List<Article> findListByColumns(Columns columns, String orderBy, Integer count);

    public List<Article> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count);

    public List<Article> findRelevantListByArticleId(long ArticleId, String status, Integer count);

    public Page<Article> _paginateByStatus(int page, int pagesize, String title, Long categoryId, String status);

    public Page<Article> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId);

    public Page<Article> _paginateByUserId(int page, int pagesize, Long userId);

    public Page<Article> paginateInNormal(int page, int pagesize);

    public Page<Article> paginateInNormal(int page, int pagesize, String orderBy);

    public Page<Article> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy);

    public void doIncArticleViewCount(long articleId);

    public void doIncArticleCommentCount(long articleId);

    public Page<Article> search(String queryString, int pageNum, int pageSize);

    public Page<Article> searchIndb(String queryString, int pageNum, int pageSize);


}