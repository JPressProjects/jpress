package io.jpress.module.article.controller;

import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/article/category")
public class ArticleCategoryController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;


    public void index() {

        if (StrUtils.isBlank(getPara())) {
            redirect("/article/category/index" + JPressHandler.getSuffix());
            return;
        }

        ArticleCategory category = getCategory();
        setAttr("category", category);
        render(category == null ? "artlist.html" : category.getHtmlView());
    }


    private ArticleCategory getCategory() {
        String idOrSlug = getPara(0);

        if (StrUtils.isBlank(idOrSlug)) {
            return null;
        }

        return StrUtils.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_CATEGORY, idOrSlug);

    }



}
