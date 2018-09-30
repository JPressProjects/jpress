package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtils;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Bean
@Singleton
public class ArticleServiceProvider extends JbootServiceBase<Article> implements ArticleService {

    @Inject
    private ArticleCategoryService acs;

    @Inject
    private UserService userService;

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
        from.append(" where m.category_id = ? ");

        return DAO.paginate(page, pagesize, select, from.toString(), categoryId);
    }

    @Override
    public Page<Article> paginateByCategoryIds(int page, int pagesize, Long[] categoryIds) {
        String select = "select * ";
        StringBuilder from = new StringBuilder("from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where m.category_id in ").append(toSqlArrayString(categoryIds));

        return DAO.paginate(page, pagesize, select, from.toString());
    }


    @Override
    public long doGetIdBySaveOrUpdateAction(Article article) {
        boolean saveOrUpdateSucess = saveOrUpdate(article);
        return saveOrUpdateSucess ? article.getId() : 0;
    }

    @Override
    public void doUpdateCategorys(long articleId, Long[] categoryIds) {

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
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
            }
        });
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
    public Page<Article> paginateByCategoryInNormal(int page, int pagesize, Long categoryId, String orderBy) {

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
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<Article> findListByColumns(Columns columns, String orderBy, Integer count) {
        return DAO.findListByColumns(columns, orderBy, count);
    }

    @Override
    public List<Article> findListByCategoryIds(Long[] categoryIds, String status, Integer count) {
        StringBuilder from = new StringBuilder("select * from article a ");
        from.append(" left join article_category_mapping m on a.id = m.`article_id` ");
        from.append(" where m.category_id in ").append(toSqlArrayString(categoryIds));

        List<Object> paras = new ArrayList<>();

        if (status != null) {
            from.append(" and status = ? ");
            paras.add(status);
        }
        if (count != null) {
            from.append(" limit " + count);
        }

        return paras.isEmpty() ? DAO.find(from.toString()) : DAO.find(from.toString(), paras.toArray());
    }


    private String toSqlArrayString(Long... ids) {
        int iMax = ids.length - 1;
        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(ids[i]));
            if (i == iMax)
                return b.append(')').toString();
            b.append(", ");
        }
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