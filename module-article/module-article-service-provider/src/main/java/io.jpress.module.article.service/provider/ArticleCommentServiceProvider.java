package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class ArticleCommentServiceProvider extends JbootServiceBase<ArticleComment> implements ArticleCommentService {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;



    @Override
    public ArticleComment findById(Object id) {
        ArticleComment comment = super.findById(id);
        articleService.join(comment, "article_id");
        userService.join(comment, "user_id");
        return comment;
    }

    @Override
    public Page<ArticleComment> paginate(int page, int pageSize) {
        Page p = super.paginate(page, pageSize);
        articleService.join(p, "article_id");
        userService.join(p, "user_id");
        return p;
    }


    @Override
    public List<ArticleComment> findListByColumns(Columns columns, String orderBy, Integer count) {
        List<ArticleComment> list = DAO.findListByColumns(columns, orderBy, count);
        articleService.join(list, "article_id");
        userService.join(list, "user_id");
        return list;
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        ArticleComment comment = findById(id);
        comment.setStatus(status);
        return comment.update();
    }


}