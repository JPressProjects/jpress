package io.jpress.module.article.directive;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.JbootRequestContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
        int pageSize = getParam("pageSize", 10, scope);
        ArticleCategory category = controller.getAttr("category");

        Page<Article> articlePage = service.paginateByCategoryId(page, pageSize, category.getId());
        scope.setGlobal("articlePage", articlePage);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("articlePaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber) {
            HttpServletRequest request = JbootRequestContext.getRequest();
            String url = request.getRequestURI();
            return doReplacePageNumber(url, pageNumber);
        }

        private static String doReplacePageNumber(String url, int pageNumber) {

            int dotIndexOf = url.lastIndexOf(".");
            int splitIndexOf = url.lastIndexOf("-");

            if (dotIndexOf < 0 & splitIndexOf < 0) {
                return url + "-" + pageNumber;
            }

            if (dotIndexOf < 0 && splitIndexOf > 0) {
                return url.substring(0, splitIndexOf) + "-" + pageNumber;
            }

            if (dotIndexOf > 0 && splitIndexOf < 0) {
                return url.substring(0, dotIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
            }

            //if (dotIndexOf > 0 && spitIndexOf >0){
            return url.substring(0, splitIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("articlePage");
        }

        public static void main(String[] args) {

            System.out.println(doReplacePageNumber("/aa/bb/cc", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc.html", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc-33-44.html", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc-333.html", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc-1.html", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc-31", 123));
            System.out.println(doReplacePageNumber("/aa/bb/cc-", 123));

        }

    }
}
