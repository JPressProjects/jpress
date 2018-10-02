package io.jpress.module.article.directive;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.JbootRequestContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("commentPage")
public class CommentPageDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCommentService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        int page = controller.getParaToInt(1, 1);
        int pageSize = getParam("pageSize", 10, scope);

        Article article = controller.getAttr("article");

        Page<ArticleComment> articlePage = service.paginateByArticleIdInNormal(page, pageSize, article.getId());
        scope.setGlobal("commentPage", articlePage);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("commentPaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber) {
            HttpServletRequest request = JbootRequestContext.getRequest();
            String url = Kits.doReplacePageNumber(request.getRequestURI(), pageNumber);
            return StrUtils.isBlank(getAnchor()) ? url : url + "#" + getAnchor();
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("commentPage");
        }

    }
}
