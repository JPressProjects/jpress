package io.jpress.module.article.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章分类：分类、专题、标签等
 * @Package io.jpress.module.article.directives
 */
@JFinalDirective("articleCategory")
public class CategoryDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Long id = getParam(0, scope);
        String type = getParam(1, scope);

        if (id == null || type == null) {
            throw new IllegalArgumentException("articleCategory error");
        }


        List<ArticleCategory> categories = categoryService.findListByType(id, type);
        if (categories == null || categories.isEmpty()) {
            return;
        }
        try {
            for (int i = 0; i < categories.size(); i++) {
                ArticleCategory category = categories.get(i);
                if (i == categories.size() - 1) {
                    writer.write(String.format("<a href=\"\">%s</a>", category.getTitle()));
                } else {
                    writer.write(String.format("<a href=\"\">%s</a> , ", category.getTitle()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
