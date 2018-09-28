package io.jpress.module.article.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/ucenter/article")
public class ArticleUCenterController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;

    @UCenterMenu(text = "文章管理", groupId = "article", order = 0)
    public void index() {
        renderText("article ucenter!!!");
    }

}
