package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.model.Article;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ArticleServiceProvider extends JbootServiceBase<Article> implements ArticleService {

    @Override
    public Page<Article> paginate(int page, int pagesize) {
        return DAO.paginate(page, pagesize);
    }

    @Override
    public long doGetIdBySaveOrUpdateAction(Article article) {
        boolean saveOrUpdateSucess = saveOrUpdate(article);
        return saveOrUpdateSucess ? article.getId() : 0;
    }
}