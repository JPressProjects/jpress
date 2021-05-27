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
import io.jpress.module.article.model.ArticleCategory;

import java.util.List;

public interface ArticleCategoryService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ArticleCategory findById(Object id);


    /**
     * find all model
     *
     * @return all <ArticleCategory
     */
    public List<ArticleCategory> findAll();


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
    public boolean delete(ArticleCategory model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(ArticleCategory model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(ArticleCategory model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ArticleCategory model);


    public List<ArticleCategory> findListByType(String type);

    public List<ArticleCategory> findTagList(String orderBy, int count);

    public Page<ArticleCategory> paginateByType(int page, int pagesize, String type);

    public List<ArticleCategory> findListByArticleId(long articleId);

    public List<ArticleCategory> findCategoryListByArticleId(long articleId);

    public List<ArticleCategory> findTagListByArticleId(long articleId);

    public List<ArticleCategory> findListByArticleId(long articleId, String type);

    public List<ArticleCategory> doNewOrFindByTagString(String[] tags);

    public List<ArticleCategory> doNewOrFindByCategoryString(String[] categories);

    public Long[] findCategoryIdsByArticleId(long articleId);

    public Long[] findCategoryIdsByParentId(long parentId);

    public ArticleCategory findFirstByTypeAndSlug(String type, String slug);

    public ArticleCategory findFirstByFlag(String flag);

    public List<ArticleCategory> findListByFlag(String flag);

    public void doUpdateArticleCount(long categoryId);

}