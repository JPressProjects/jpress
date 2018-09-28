package io.jpress.module.article;

import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.db.model.Columns;
import io.jpress.core.module.Module;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page
 */
public class ArticleModule extends Module {

    private String id = "article";
    private String text = "文章";

    private ArticleModule() {
        addAdminMenu(id, text, "<i class=\"fa fa-fw fa-file-text\"></i>", 1);
        addUcenterMenu(id, text, "<i class=\"fa fa-fw fa-file-text\"></i>", 1);
    }

    private static final ArticleModule me = new ArticleModule();

    public static ArticleModule me() {
        return me;
    }

    @Override
    public String onGetDashboardHtmlBox(Controller controller) {
        List<Article> articles = Jboot.bean(ArticleService.class).findListByColumns(Columns.create(), "id desc", 10);
        controller.setAttr("articles", articles);

        ArticleCommentService commentService = Jboot.bean(ArticleCommentService.class);
        List<ArticleComment> articleComments = commentService.findListByColumns(Columns.create().ne("status", ArticleComment.STATUS_TRASH), "id desc", 10);
        controller.setAttr("articleComments", articleComments);

        return "article/_dashboard_box.html";
    }
}
