package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 相关文章
 * @Package io.jpress.module.article.directive
 */
@JFinalDirective("relevantArticles")
public class RelevantArticlesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Article article = getParam(0, scope);
        if (article == null) {
            throw new IllegalArgumentException("#relevantArticles(...) argument must not be null or empty");
        }

        List<ArticleCategory> categories = categoryService.findTagListByArticleId(article.getId());
        if (categories == null || categories.isEmpty()) {
            return;
        }

        List<Long> tagIds = new ArrayList<>();
        for (ArticleCategory category : categories) {
            if (category.isTag()) {
                tagIds.add(category.getId());
            }
        }

        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        List<Article> relevantArticles = service.findListByCategoryIds(tagIds.toArray(new Long[0]), Article.STATUS_NORMAL, 3);
        if (relevantArticles != null && relevantArticles.isEmpty()) {
            scope.setLocal("relevantArticles", relevantArticles);
            renderBody(env, scope, writer);
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
