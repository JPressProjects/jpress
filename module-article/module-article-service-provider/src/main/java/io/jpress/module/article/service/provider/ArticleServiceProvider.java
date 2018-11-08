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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtils;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.task.ArticleCommentsCountUpdateTask;
import io.jpress.module.article.service.task.ArticleViewsCountUpdateTask;
import io.jpress.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Bean
@Singleton
public class ArticleServiceProvider extends JbootServiceBase<Article> implements ArticleService {

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private UserService userService;

    @Inject
    private ArticleCommentService commentService;

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public Page<Article> paginate(int page, int pagesize) {
        Page<Article> articlePage = DAO.paginate(page, pagesize, "id desc");
        userService.join(articlePage, "user_id");

        return articlePage;
    }


    @Override
    public Page<Article> paginateByCategoryId(int page, int pagesize, long categoryId) {
        String select = "select * ";
        StringBuilder from = new StringBuilder("from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where a.status = ? and m.category_id = ? ");

        return DAO.paginate(page, pagesize, select, from.toString(), Article.STATUS_NORMAL, categoryId);
    }

    @Override
    public Page<Article> paginateByCategoryIds(int page, int pagesize, Long[] categoryIds) {
        String select = "select * ";
        StringBuilder from = new StringBuilder("from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where m.category_id in ").append(SqlUtils.buildInSqlPara(categoryIds));

        return DAO.paginate(page, pagesize, select, from.toString());
    }

    @Override
    public boolean deleteById(Object id) {
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

    @Override
    public long doGetIdBySaveOrUpdateAction(Article article) {
        boolean saveOrUpdateSucess = saveOrUpdate(article);
        return saveOrUpdateSucess ? article.getId() : 0;
    }

    @Override
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


        StringBuilder sqlBuilder = new StringBuilder("from article a ");
        if (categoryId != null) {
            sqlBuilder.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        }

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("a.status", status);
        SqlUtils.likeAppend(columns, "a.title", title);

        SqlUtils.appendWhereByColumns(columns, sqlBuilder);
        sqlBuilder.append(" order by id desc ");

        Page<Article> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserPage(dataPage);
    }

    @Override
    public Page<Article> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId) {


        StringBuilder sqlBuilder = new StringBuilder("from article a ");
        if (categoryId != null) {
            sqlBuilder.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        }

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.ne("a.status", Article.STATUS_TRASH);

        SqlUtils.likeAppend(columns, "a.title", title);

        SqlUtils.appendWhereByColumns(columns, sqlBuilder);
        sqlBuilder.append(" order by id desc ");

        Page<Article> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserPage(dataPage);
    }

    @Override
    public Page<Article> _paginateByUserId(int page, int pagesize, Long userId) {
        return DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), "id desc");
    }

    @Override
    public Page<Article> paginateInNormal(int page, int pagesize) {

        Columns columns = new Columns();
        columns.add("status", Article.STATUS_NORMAL);

        Page<Article> dataPage = DAO.paginateByColumns(page, pagesize, columns, "id desc");
        return joinUserPage(dataPage);
    }

    @Override
    public Page<Article> paginateInNormal(int page, int pagesize, String orderBy) {

        if (StrUtils.isBlank(orderBy)) {
            orderBy = "id desc";
        }

        Columns columns = new Columns();
        columns.add("status", Article.STATUS_NORMAL);

        Page<Article> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserPage(dataPage);
    }


    @Override
    public Page<Article> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        StringBuilder sqlBuilder = new StringBuilder("from article a ");
        sqlBuilder.append(" left join article_category_mapping m on a.id = m.`article_id` ");

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("a.status", Article.STATUS_NORMAL);

        SqlUtils.appendWhereByColumns(columns, sqlBuilder);

        buildOrderBySQL(sqlBuilder, orderBy);

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
    public List<Article> findListByColumns(Columns columns, String orderBy, Integer count) {
        return DAO.findListByColumns(columns, orderBy, count);
    }

    @Override
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
    public List<Article> findRelevantListByArticleId(long articleId, String status, Integer count) {

        List<ArticleCategory> tags = categoryService.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        List<Long> tagIds = new ArrayList<>();
        for (ArticleCategory category : tags) {
            tagIds.add(category.getId());
        }


        StringBuilder from = new StringBuilder("select * from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where m.category_id in ").append(SqlUtils.buildInSqlPara(tagIds.toArray()));
        from.append(" and a.id != ? ");


        List<Object> paras = new ArrayList<>();
        paras.add(articleId);

        if (status != null) {
            from.append(" and status = ? ");
            paras.add(status);
        }

        from.append(" group by a.id");

        if (count != null) {
            from.append(" limit " + count);
        }

        return DAO.find(from.toString(), paras.toArray());
    }


    /**
     * 此方法的主要作用是限制 用户传其他orderby的字段，
     * 同时因为orderby是前端传入的数据，防止sql注入
     *
     * @param sqlBuilder
     * @param orderBy
     */
    private static void buildOrderBySQL(StringBuilder sqlBuilder, String orderBy) {

        if (StrUtils.isBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY a.id DESC");
            return;
        }

        // maybe orderby == "view_count desc";
        String orderbyInfo[] = orderBy.trim().split("\\s+");

        //不合法的orderby
        if (orderbyInfo.length < 1 || orderbyInfo.length > 2) {
            sqlBuilder.append(" ORDER BY a.id DESC");
            return;
        }

        orderBy = orderbyInfo[0];

        /**
         * 根据ID排序
         */
        if ("id".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.id ");
        }
        /**
         * 根据浏览量排序
         */
        else if ("view_count".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.view_count ");
        }
        /**
         * 根据评论量排序
         */
        else if ("comment_count".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.comment_count ");
        }
        /**
         * 根据文章的更新时间排序
         */
        else if ("modified".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.modified ");
        }

        /**
         * 根据后台排序数字进行排序
         */
        else if ("order_number".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.order_number ");
        }
        /**
         * 根据最后评论时间排序
         */
        else if ("comment_time".equals(orderBy)) {
            sqlBuilder.append(" ORDER BY a.comment_time ");
        }

        /**
         * 根据创建时间排序
         */
        else {
            sqlBuilder.append(" ORDER BY a.created ");
        }

        if (orderbyInfo.length == 1) {
            sqlBuilder.append(" DESC ");
        } else {
            sqlBuilder.append(orderbyInfo[1]);
        }

    }

}