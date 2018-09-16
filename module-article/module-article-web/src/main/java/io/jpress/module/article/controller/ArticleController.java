package io.jpress.module.article.controller;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.TemplateControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/article")
public class ArticleController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;
    @Inject
    private ArticleCategoryService categoryService;

    public void index() {
        Article article = getArticle();
        if (article == null) {
            renderError(404);
            return;
        }

        Long page = getParaToLong(1);

        setAttr("article", article);
        render(article.getHtmlView());
    }

    private Article getArticle() {
        String idOrSlug = getPara(0);
        return StringUtils.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(idOrSlug);
    }


    public void category() {
        ArticleCategory category = getArticleCategory(ArticleCategory.TYPE_CATEGORY);
        setAttr("category", category);
        render(category.getHtmlView());
    }


    public void subject() {
        ArticleCategory category = getArticleCategory(ArticleCategory.TYPE_SUBJECT);
        setAttr("category", category);
        render(category.getHtmlView());
    }

    public void tag() {
        ArticleCategory category = getArticleCategory(ArticleCategory.TYPE_TAG);
        setAttr("category", category);
        render(category.getHtmlView());
    }

    private ArticleCategory getArticleCategory(String type) {
        String idOrSlug = getPara(0);
        ArticleCategory category = StringUtils.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(type, idOrSlug);

        if (category == null) {
            renderError(404);
            return null;
        }

        return category;
    }


}
