package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
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
        scope.setLocal("categories", categories);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
