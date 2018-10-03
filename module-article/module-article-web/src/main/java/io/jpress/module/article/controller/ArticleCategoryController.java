package io.jpress.module.article.controller;

import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
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
    private ArticleCategoryService categoryService;


    public void index() {

        if (StrUtils.isBlank(getPara())) {
            redirect("/article/category/index" + JPressHandler.getSuffix());
            return;
        }

        ArticleCategory category = getCategory();
        setAttr("category", category);
        setSeoInfos(category);

        render(getRenderView(category));
    }

    private void setSeoInfos(ArticleCategory category) {
        if (category == null) {
            return;
        }

        setSeoTitle(category.getTitle());
        setSeoKeywords(category.getMetaKeywords());
        setSeoDescription(StrUtils.isBlank(category.getMetaDescription())
                ? CommonsUtils.maxLength(category.getContent(), 100)
                : category.getMetaDescription());
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

    private String getRenderView(ArticleCategory category) {
        return category == null
                ? "artlist.html"
                : category.getHtmlView();
    }


}
