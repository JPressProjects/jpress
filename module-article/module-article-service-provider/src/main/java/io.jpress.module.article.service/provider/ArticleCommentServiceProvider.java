package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.model.ArticleComment;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
public class ArticleCommentServiceProvider extends JbootServiceBase<ArticleComment> implements ArticleCommentService {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;

    @Override
    public Page<ArticleComment> paginate(int page, int pageSize) {
        Page p = super.paginate(page, pageSize);
        articleService.join(p, "article_id");
        userService.join(p, "user_id");
        return p;
    }
}