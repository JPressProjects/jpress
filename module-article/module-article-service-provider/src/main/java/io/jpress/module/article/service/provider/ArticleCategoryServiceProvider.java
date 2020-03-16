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
package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.CacheTime;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.Copyer;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class ArticleCategoryServiceProvider extends JbootServiceBase<ArticleCategory> implements ArticleCategoryService {


    @Override
    @Cacheable(name = "articleCategory")
    public List<ArticleCategory> findAll() {
        return super.findAll();
    }

    @Override
    @CachesEvict({
            @CacheEvict(name = "articleCategory", key = "*"),
            @CacheEvict(name = "article-category", key = "*"),
    })
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }

    @Override
    public List<ArticleCategory> findListByType(String type) {
        return Copyer.copy(findListByTypeWithCache(type));
    }

    @Cacheable(name = "articleCategory", key = "type:#(type)")
    public List<ArticleCategory> findListByTypeWithCache(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "order_number asc,id desc");
    }


    @Override
    public List<ArticleCategory> findTagList(String orderBy, int count) {
        return DAO.findListByColumns(Columns.create("type", ArticleCategory.TYPE_TAG), orderBy, count);
    }


    @Override
    public Page<ArticleCategory> paginateByType(int page, int pagesize, String type) {
        return DAO.paginateByColumn(page, pagesize, Column.create("type", type), "order_number asc,id desc");
    }

    @Override
    @Cacheable(name = "article-category", key = "#(articleId)", liveSeconds = 2 * CacheTime.HOUR, nullCacheEnable = true)
    public List<ArticleCategory> findListByArticleId(long articleId) {
        List<Record> mappings = Db.find("select * from article_category_mapping where article_id = ?", articleId);
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }

        return mappings
                .stream()
                .map(record -> DAO.findById(record.get("category_id")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleCategory> findCategoryListByArticleId(long articleId) {
        return findListByArticleId(articleId, ArticleCategory.TYPE_CATEGORY);
    }

    @Override
    public List<ArticleCategory> findTagListByArticleId(long articleId) {
        return findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
    }

    @Override
    public List<ArticleCategory> findListByArticleId(long articleId, String type) {
        List<ArticleCategory> categoryList = findListByArticleId(articleId);
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        }
        return categoryList
                .stream()
                .filter(category -> type.equals(category.getType()))
                .collect(Collectors.toList());
    }


    @Override
    public boolean deleteById(Object id) {
        Db.update("delete from article_category_mapping where category_id = ?", id);
        return super.deleteById(id);
    }


    @Override
    public void doUpdateArticleCount(long categoryId) {
        long articleCount = Db.queryLong("select count(*) from article_category_mapping where category_id = ? ", categoryId);
        ArticleCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(articleCount);
            update(category);
        }
    }


    @Override
    public List<ArticleCategory> doNewOrFindByTagString(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        boolean needClearCache = false;
        List<ArticleCategory> articleCategories = new ArrayList<>();
        for (String tag : tags) {

            if (StrUtil.isBlank(tag)) {
                continue;
            }

            //slug不能包含字符串点 " . "，否则url不能被访问
            String slug = tag.contains(".")
                    ? tag.replace(".", "_")
                    : tag;

            Columns columns = Columns.create("type", ArticleCategory.TYPE_TAG);
            columns.add(Column.create("slug", slug));

            ArticleCategory articleCategory = DAO.findFirstByColumns(columns);

            if (articleCategory == null) {
                articleCategory = new ArticleCategory();
                articleCategory.setTitle(tag);
                articleCategory.setSlug(slug);
                articleCategory.setType(ArticleCategory.TYPE_TAG);
                articleCategory.save();

                needClearCache = true;
            }

            articleCategories.add(articleCategory);
        }

        if (needClearCache) {
            AopCache.removeAll("articleCategory");
        }

        return articleCategories;
    }

    @Override
    public List<ArticleCategory> doNewOrFindByCategoryString(String[] categories) {
        if (categories == null || categories.length == 0) {
            return null;
        }

        List<ArticleCategory> articleCategories = new ArrayList<>();
        for (String category : categories) {
            Columns columns = Columns.create("type", ArticleCategory.TYPE_CATEGORY);
            columns.add(Column.create("slug", category));

            ArticleCategory articleCategory = DAO.findFirstByColumns(columns);

            if (articleCategory == null) {
                articleCategory = new ArticleCategory();
                articleCategory.setTitle(category);
                articleCategory.setSlug(category);
                articleCategory.setType(ArticleCategory.TYPE_CATEGORY);
                articleCategory.save();
            }

            articleCategories.add(articleCategory);
        }

        return articleCategories;
    }

    @Override
    public Long[] findCategoryIdsByArticleId(long articleId) {
        List<Record> records = Db.find("select * from article_category_mapping where article_id = ?", articleId);
        if (records == null || records.isEmpty()) {
            return null;
        }

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }

    @Override
    public Long[] findCategoryIdsByParentId(long parentId) {
        List<ArticleCategory> categories = findAll();
        List<ArticleCategory> findedList = new ArrayList<>();
        findedList.add(findById(parentId));
        findChild(parentId, categories, findedList);
        return ArrayUtils.toObject(findedList.stream().mapToLong(category -> category.getId()).toArray());
    }

    public void findChild(Long parentId, List<ArticleCategory> from, List<ArticleCategory> to) {
        for (ArticleCategory category : from) {
            if (parentId.equals(category.getParentId())) {
                to.add(category);
                findChild(category.getId(), from, to);
            }
        }
    }


    @Override
    public ArticleCategory findFirstByTypeAndSlug(String type, String slug) {
        return DAO.findFirstByColumns(Columns.create("type", type).eq("slug", slug));
    }

    @Override
    public ArticleCategory findFirstByFlag(String flag) {
        return DAO.findFirstByColumns(Columns.create("flag", flag));
    }

    @Override
    public List<ArticleCategory> findListByFlag(String flag) {
        return DAO.findListByColumns(Columns.create("flag", flag));
    }

}