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

import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.search.ArticleSearcher;
import io.jpress.module.article.service.search.ArticleSearcherFactory;
import io.jpress.module.article.service.sitemap.ArticleSitemapManager;
import io.jpress.module.article.service.task.ArticleCommentsCountUpdateTask;
import io.jpress.module.article.service.task.ArticleViewsCountUpdateTask;
import io.jpress.service.UserService;
import io.jpress.web.seoping.SeoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                , Columns.create("article.status", status));
    }

    @Override
    public Page<Article> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId) {

        return _paginateByBaseColumns(page
                , pagesize
                , title
                , categoryId
                , Columns.create().ne("article.status", Article.STATUS_TRASH));
    }


    public Page<Article> _paginateByBaseColumns(int page, int pagesize, String title, Long categoryId, Columns baseColumns) {

        Columns columns = baseColumns;
        columns.add("m.category_id", categoryId);
        columns.likeAppendPercent("article.title", title);

        Page<Article> dataPage = DAO.leftJoinIf("article_category_mapping", categoryId != null)
                .as("m")
                .on("article.id = m.article_id")
                .paginateByColumns(page, pagesize, columns, "id desc");


        return joinUserInfo(dataPage);
    }

    @Override
    public Page<Article> _paginateByUserId(int page, int pagesize, Long userId) {
        return DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), DEFAULT_ORDER_BY);
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateInNormal(int page, int pagesize) {
        return paginateInNormal(page, pagesize, "id desc");
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateInNormal(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.add("status", Article.STATUS_NORMAL);
        Page<Article> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserInfo(dataPage);
    }


    @Override
    @Cacheable(name = "articles")
    public Page<Article> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("article.status", Article.STATUS_NORMAL);

        Page<Article> dataPage = DAO.leftJoin("article_category_mapping")
                .as("m").on("article.id=m.`article_id`")
                .paginateByColumns(page, pagesize, columns, StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY));
        return joinUserInfo(dataPage);
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
    public Page<Article> search(String queryString, int pageNum, int pageSize) {
        try {
            ArticleSearcher searcher = ArticleSearcherFactory.getSearcher();
            Page<Article> page = searcher.search(queryString, pageNum, pageSize);
            if (page != null) {
                return page;
            }
        } catch (Exception ex) {
            LogKit.error(ex.toString(), ex);
        }
        return new Page<>(new ArrayList<>(), pageNum, pageSize, 0, 0);
    }

    @Override
    @Cacheable(name = "articles")
    public Page<Article> searchIndb(String queryString, int pageNum, int pageSize) {
        Columns columns = Columns.create("status", Article.STATUS_NORMAL)
                .likeAppendPercent("title", queryString);
        return joinUserInfo(paginateByColumns(pageNum, pageSize, columns, "order_number desc,id desc"));
    }


    private Page<Article> joinUserInfo(Page<Article> page) {
        userService.join(page, "user_id");
        return page;
    }

    private List<Article> joinUserInfo(List<Article> list) {
        userService.join(list, "user_id");
        return list;
    }

    private Article joinUserInfo(Article article) {
        userService.join(article, "user_id");
        return article;
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public boolean doChangeStatus(long id, String status) {
        Article article = findById(id);
        article.setStatus(status);
        return update(article);
    }

    @Override
    public Long findCountByStatus(String status) {
        return DAO.findCountByColumn(Column.create("status", status));
    }

    @Override
    public Article findById(Object id) {
        return joinUserInfo(super.findById(id));
    }

    @Override
    public Article findByTitle(String title) {
        return joinUserInfo(DAO.findFirstByColumn(Column.create("title", title)));
    }

    @Override
    public Article findFirstBySlug(String slug) {
        return joinUserInfo(DAO.findFirstByColumn(Column.create("slug", slug)));
    }


    @Override
    public Article findNextById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_GT));
        columns.add(Column.create("status", Article.STATUS_NORMAL));
        return joinUserInfo(DAO.findFirstByColumns(columns));
    }

    @Override
    public Article findPreviousById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_LT));
        columns.add(Column.create("status", Article.STATUS_NORMAL));
        return joinUserInfo(DAO.findFirstByColumns(columns, "id desc"));
    }

    @Override
    @Cacheable(name = "articles", key = "#(columns.cacheKey)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Article> findListByColumns(Columns columns, String orderBy, Integer count) {
        return joinUserInfo(DAO.findListByColumns(columns, orderBy, count));
    }

    @Override
    @Cacheable(name = "articles", key = "findListByCategoryId:#(categoryId)-#(hasThumbnail)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Article> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count) {

//        StringBuilder from = new StringBuilder("select * from article a ");
//        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
//        from.append(" where m.category_id = ? ");
//        from.append(" and a.status = ? ");
//
//
//        if (hasThumbnail != null) {
//            if (hasThumbnail == true) {
//                from.append(" and a.thumbnail is not null");
//            } else {
//                from.append(" and a.thumbnail is null");
//            }
//        }
//
//        from.append(" group by a.id ");
//
//        if (orderBy != null) {
//            from.append(" order by " + orderBy);
//        }
//
//        if (count != null) {
//            from.append(" limit " + count);
//        }

        Columns columns = Columns
                .create("m.category_id",categoryId)
                .eq("article.status",Article.STATUS_NORMAL);

        if (hasThumbnail != null){
            if (hasThumbnail){
                columns.isNotNull("article.thumbnail");
            }else {
                columns.isNull("article.thumbnail");
            }
        }

        columns.string("group by article.id");


        List<Article> articles = DAO
                .leftJoin("article_category_mapping").as("m")
                .on("article.id = m.`article_id`")
                .findListByColumns(columns,orderBy);

        return joinUserInfo(articles);
    }

    @Override
    @Cacheable(name = "articles")
    public List<Article> findRelevantListByArticleId(long articleId, String status, Integer count) {

        List<ArticleCategory> tags = categoryService.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        List<Long> tagIds = tags.stream().map(category -> category.getId()).collect(Collectors.toList());

        Columns columns = Columns.create();
        columns.in("m.category_id", tagIds.toArray());
        columns.ne("article.id", articleId);
        columns.eq("article.status", status);

        List<Article> articles = DAO.leftJoin("article_category_mapping")
                .as("m")
                .on("article.id = m.`article_id`")
                .findListByColumns(columns, count);

        return joinUserInfo(articles);
    }


    @Override
    public Object save(Article model) {
        Object id = super.save(model);
        if (id != null && model.isNormal()) {
            ArticleSearcherFactory.getSearcher().addArticle(model);
            ArticleSitemapManager.me().rebuild();
            SeoManager.me().ping(model.toPingData());
            SeoManager.me().baiduPush(model.getUrl());
        }
        return id;
    }

    @Override
    public boolean update(Article model) {
        boolean success = super.update(model);
        if (success) {

            ArticleSitemapManager.me().rebuild();

            if (model.isNormal()) {
                ArticleSearcherFactory.getSearcher().updateArticle(model);
                SeoManager.me().ping(model.toPingData());
                SeoManager.me().baiduUpdate(model.getUrl());
            } else {
                ArticleSearcherFactory.getSearcher().deleteArticle(model.getId());
            }
        }
        return success;
    }


    @Override
    public boolean delete(Article model) {
        boolean success = super.delete(model);
        if (success) {
            ArticleSearcherFactory.getSearcher().deleteArticle(model.getId());
            ArticleSitemapManager.me().rebuild();
        }
        return success;
    }

    @Override
    @CacheEvict(name = "articles", key = "*")
    public void removeCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }


    @Override

    @CachesEvict({
            @CacheEvict(name = "articles", key = "*"),
            @CacheEvict(name = "article-category", key = "#(articleId)"),
    })
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
    public boolean deleteById(Object id) {

        //搜索搜索引擎的内容
        ArticleSearcherFactory.getSearcher().deleteArticle(id);

        return Db.tx(() -> {
            boolean delOk = ArticleServiceProvider.super.deleteById(id);
            if (delOk == false) {
                return false;
            }

            //删除文章的管理分类
            List<Record> records = Db.find("select * from article_category_mapping where article_id = ? ", id);
            if (records != null &&  !records.isEmpty()) {
                //更新文章数量
                Db.update("delete from article_category_mapping where article_id = ?", id);
                records.forEach(record -> categoryService.doUpdateArticleCount(record.get("category_id")));
            }


            //删除文章的所有评论
            commentService.deleteByArticleId(id);

            return true;
        });
    }


    @Override
    @CachesEvict({
            @CacheEvict(name = "articles", key = "*"),
            @CacheEvict(name = "article-category", key = "#(id)", unless = "id == null"),
    })
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }
}