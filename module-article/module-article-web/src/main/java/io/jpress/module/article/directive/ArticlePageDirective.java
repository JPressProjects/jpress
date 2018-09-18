package io.jpress.module.article.directive;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("articlePage")
public class ArticlePageDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        int page = controller.getParaToInt(1, 1);
        ArticleCategory category = controller.getAttr("category");

        Page<Article> articlePage = service.paginateByCategoryId(page, 10, category.getId());
        scope.setLocal("articlePage", articlePage);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
