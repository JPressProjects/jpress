package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("article")
public class ArticleDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String idOrSlug = getParam(0, scope);
        Article article = getArticle(idOrSlug);

        if (article == null) {
            return;
        }

        scope.setLocal("article", article);
        renderBody(env, scope, writer);
    }

    private Article getArticle(String idOrSlug) {
        return StrUtils.isNumeric(idOrSlug)
                ? service.findById(idOrSlug)
                : service.findFirstBySlug(idOrSlug);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
