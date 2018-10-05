package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressConsts;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章分类：分类、专题、标签等
 * @Package io.jpress.module.article.directives
 */
@JFinalDirective("categories")
public class CategoriesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String type = getParam(0, ArticleCategory.TYPE_CATEGORY, scope);

        if (type == null) {
            throw new IllegalArgumentException("#categories(type) is error");
        }

        List<ArticleCategory> categories = categoryService.findListByType(type);
        if (categories == null || categories.isEmpty()) {
            return;
        }

        doFlagIsActiveByCurrentCategory(categories);
        doFlagIsActiveByCurrentArticle(categories);

        scope.setLocal("categories", categories);
        renderBody(env, scope, writer);
    }


    private void doFlagIsActiveByCurrentCategory(List<ArticleCategory> categories) {

        ArticleCategory currentCategory = JbootControllerContext.get().getAttr("category");

        //当前页面并不是某个分类页面
        if (currentCategory == null) {
            return;
        }

        doFlagByCurrentCategory(categories, currentCategory);

    }


    private void doFlagIsActiveByCurrentArticle(List<ArticleCategory> categories) {
        Article currentArticle = JbootControllerContext.get().getAttr("article");

        //当前页面并不是文章详情页面
        if (currentArticle == null) {
            return;
        }

        List<ArticleCategory> articleCategories = categoryService.findActiveCategoryListByArticleId(currentArticle.getId());
        if (articleCategories == null || articleCategories.isEmpty()) {
            return;
        }

        for (ArticleCategory articleCategory : articleCategories) {
            doFlagByCurrentCategory(categories, articleCategory);
        }
    }


    private void doFlagByCurrentCategory(List<ArticleCategory> categories, ArticleCategory currentCategory) {
        for (ArticleCategory category : categories) {
            if (currentCategory.getId().equals(category.getId())) {
                JPressConsts.doFlagModelActive(category);
            }
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
