package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("articles")
public class ArticlesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getParam("flag", scope);
        Boolean hasThumbnail = getParam("hasThumbnail", scope);
        String orderBy = getParam("orderBy", "id desc", scope);
        int count = getParam("count", 10, scope);


        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.is_not_null("thumbnail");
            } else {
                columns.is_null("thumbnail");
            }
        }

        List<Article> articles = service.findListByColumns(columns, orderBy, count);
        scope.setLocal("articles", articles);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
