package io.jpress.module.article.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.UcenterControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/ucenter/article")
public class ArticleUCenterController extends UcenterControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;

    @UCenterMenu(text = "文章列表", groupId = "article", order = 0)
    public void index() {
        render("/WEB-INF/views/ucenter/article/article_list.html");
    }

    @UCenterMenu(text = "投稿", groupId = "article", order = 1)
    public void write() {
        render("/WEB-INF/views/ucenter/article/article_write.html");
    }


    @UCenterMenu(text = "评论列表", groupId = "comment", order = 0)
    public void comment() {
        render("/WEB-INF/views/ucenter/article/comment_list.html");
    }

}
