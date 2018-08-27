package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;

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

    @Override
    public Page<Article> paginate(int page, int pagesize) {
        return DAO.paginate(page, pagesize);
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

}