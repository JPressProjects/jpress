package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
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
    public Page<Article> paginateByStatus(int page, int pagesize, String status) {
        return DAO.paginateByColumn(page,
                pagesize,
                Column.create("status", status),
                "id desc");
    }

    @Override
    public Page<Article> paginateWithoutTrash(int page, int pagesize) {
        return DAO.paginateByColumn(page,
                pagesize,
                Column.create("status", Article.STATUS_TRASH, Column.LOGIC_NOT_EQUALS),
                "id desc");
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

}