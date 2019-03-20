/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.search.ArticleSearcher;
import io.jpress.module.article.service.search.ArticleSearcherFactory;
import io.jpress.module.article.service.task.ArticleCommentsCountUpdateTask;
import io.jpress.module.article.service.task.ArticleViewsCountUpdateTask;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ArticleServiceProvider extends JbootServiceBase<Article> implements ArticleService {

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private UserService userService;

    @Inject
    private ArticleCommentService commentService;

    private static final String DEFAULT_ORDER_BY = "order_number desc,id desc";

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }


    @Override
    public void doUpdateCommentCount(long articleId) {
        Article article = findById(articleId);
        if (article == null) {
            return;
        }

        long count = commentService.findCountByArticleId(articleId);
        article.setCommentCount(count);
        article.update();
    }

    @Override
    public Page<Article> _paginateByStatus(int page, int pagesize, String title, Long categoryId, String status) {

        return _paginateByBaseColumns(page
                , pagesize
                , title
                , categoryId
                , Columns.create("a.status", status));
    }

    @Override
    public Page<Article> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId) {

        return _paginateByBaseColumns(page
                , pagesize
                , title
                , categoryId
                , Columns.create().ne("a.status", Article.STATUS_TRASH));
    }


    public Page<Article> _paginateByBaseColumns(int page, int pagesize, String title, Long categoryId, Columns baseColumns) {


        StringBuilder sqlBuilder = new StringBuilder("from article a ");
        if (categoryId != null) {
            sqlBuilder.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        }

        Columns columns = baseColumns;
        columns.add("m.category_id", categoryId);
        columns.likeAppendPercent("a.title", title);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));

        // 前台走默认排序，但是后台必须走 id 排序，
        // 否当有默认排序的文章很多的时候,发布的新文章可能在后几页
        sqlBuilder.append(" order by id desc");

        Page<Article> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserPage(dataPage);
    }

    @Override
    public Page<Article> _paginateByUserId(int page, int pagesize, Long userId) {
        return DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), DEFAULT_ORDER_BY);
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateInNormal(int page, int pagesize) {
        return paginateInNormal(page, pagesize, null);
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateInNormal(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.add("status", Article.STATUS_NORMAL);
        Page<Article> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserPage(dataPage);
    }


    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        StringBuilder sqlBuilder = new StringBuilder("from article a ");
        sqlBuilder.append(" left join article_category_mapping m on a.id = m.`article_id` ");

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("a.status", Article.STATUS_NORMAL);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));

        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        sqlBuilder.append(" ORDER BY ").append(orderBy);

        Page<Article> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserPage(dataPage);
    }

    @Override
    public void doIncArticleViewCount(long articleId) {
        ArticleViewsCountUpdateTask.recordCount(articleId);
    }

    @Override
    public void doIncArticleCommentCount(long articleId) {
        ArticleCommentsCountUpdateTask.recordCount(articleId);
    }


    @Override
    public boolean isOwn(Article article, long userId) {
        if (article == null) {
            return false;
        }

        if (article.getId() == null) {
            //谁都可以用于一篇没有主键的文章，因为这篇文章处于编辑中，还未保存到数据库
            return true;
        }

        if (article.getUserId() == null) {
            return false;
        }

        return article.getUserId().equals(userId);
    }

    @Override
    public Page<Article> search(String queryString, int pageNum, int pageSize) {
        try {
            ArticleSearcher searcher = ArticleSearcherFactory.getSearcher();
            return searcher.search(queryString, pageNum, pageSize);
        } catch (Exception ex) {
            LogKit.error(ex.toString(), ex);
        }
        return null;
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> searchIndb(String queryString, int pageNum, int pageSize) {
        Columns columns = Columns.create("status", Article.STATUS_NORMAL)
                .likeAppendPercent("title", queryString);
        return joinUserPage(paginateByColumns(pageNum, pageSize, columns, "order_number desc,id desc"));
    }


    private Page<Article> joinUserPage(Page<Article> page) {
        userService.join(page, "user_id");
        return page;
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        Article article = findById(id);
        article.setStatus(status);
        return article.update();
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from article where status = ?", status);
    }

    @Override
    public Article findFirstBySlug(String slug) {
        return DAO.findFirstByColumn(Column.create("slug", slug));
    }


    @Override
    public Article findNextById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_GT));
        columns.add(Column.create("status", Article.STATUS_NORMAL));
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public Article findPreviousById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_LT));
        columns.add(Column.create("status", Article.STATUS_NORMAL));
        return DAO.findFirstByColumns(columns, "id desc");
    }

    @Override
    @Cacheable(name = "articles", key = "#(columns.cacheKey)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Article> findListByColumns(Columns columns, String orderBy, Integer count) {
        return DAO.findListByColumns(columns, orderBy, count);
    }

    @Override
    @Cacheable(name = "articles", key = "findListByCategoryId:#(categoryId)-#(hasThumbnail)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Article> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count) {

        StringBuilder from = new StringBuilder("select * from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where m.category_id = ? ");
        from.append(" and a.status = ? ");


        if (hasThumbnail != null) {
            if (hasThumbnail == true) {
                from.append(" and a.thumbnail is not null");
            } else {
                from.append(" and a.thumbnail is null");
            }
        }

        from.append(" group by a.id ");

        if (orderBy != null) {
            from.append(" order by " + orderBy);
        }

        if (count != null) {
            from.append(" limit " + count);
        }

        return DAO.find(from.toString(), categoryId, Article.STATUS_NORMAL);
    }

    @Override
    @Cacheable(name = "articles")
    public List<Article> findRelevantListByArticleId(long articleId, String status, Integer count) {

        List<ArticleCategory> tags = categoryService.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        List<Long> tagIds = new ArrayList<>();
        for (ArticleCategory category : tags) {
            tagIds.add(category.getId());
        }

        Columns columns = Columns.create();
        columns.in("m.category_id", tagIds.toArray());
        columns.ne("a.id", articleId);
        columns.eq("status", status);

        StringBuilder from = new StringBuilder("select * from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(SqlUtils.toWhereSql(columns));
        from.append(" group by a.id");

        if (count != null) {
            from.append(" limit " + count);
        }

        return DAO.find(from.toString(), columns.getValueArray());
    }


    @Override
    @CacheEvict(name = "articles", key = "*")
    public Object save(Article model) {
        Object id = super.save(model);
        if (id != null && model.isNormal()) {
            ArticleSearcherFactory.getSearcher().addArticle(model);
        }
        return id;
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public boolean update(Article model) {
        boolean success = super.update(model);
        if (success) {
            if (model.isNormal()) {
                ArticleSearcherFactory.getSearcher().updateArticle(model);
            } else {
                ArticleSearcherFactory.getSearcher().deleteArticle(model.getId());
            }
        }
        return success;
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public boolean delete(Article model) {
        boolean success = super.delete(model);
        if (success) {
            ArticleSearcherFactory.getSearcher().deleteArticle(model.getId());
        }
        return success;
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public void deleteCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }


    @Override
    @CacheEvict(name = "articles", key = "*")
    public void doUpdateCategorys(long articleId, Long[] categoryIds) {

        Db.tx(() -> {
            Db.update("delete from article_category_mapping where article_id = ?", articleId);

            if (categoryIds != null && categoryIds.length > 0) {
                List<Record> records = new ArrayList<>();
                for (long categoryId : categoryIds) {
                    Record record = new Record();
                    record.set("article_id", articleId);
                    record.set("category_id", categoryId);
                    records.add(record);
                }
                Db.batchSave("article_category_mapping", records, records.size());
            }

            return true;
        });
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public boolean deleteById(Object id) {

        ArticleSearcherFactory.getSearcher().deleteArticle(id);

        return Db.tx(() -> {
            boolean delOk = ArticleServiceProvider.super.deleteById(id);
            if (delOk == false) {
                return false;
            }

            List<Record> records = Db.find("select * from article_category_mapping where article_id = ? ", id);
            if (records == null || records.isEmpty()) {
                return true;
            }

            Db.update("delete from article_category_mapping where article_id = ?", id);

            records.stream().forEach(record -> {
                categoryService.updateCount(record.get("category_id"));
            });

            return true;
        });
    }


}