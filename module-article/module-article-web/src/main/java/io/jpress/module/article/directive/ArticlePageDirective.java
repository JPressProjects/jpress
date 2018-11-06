/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.module.article.directive;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
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
        int pageSize = getPara("pageSize", scope, 10);

        ArticleCategory category = controller.getAttr("category");


        Page<Article> articlePage = category == null ?
                service.paginateInNormal(page, pageSize) :
                service.paginateByCategoryIdInNormal(page, pageSize, category.getId(), null);

        scope.setGlobal("articlePage", articlePage);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("articlePaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        private boolean forIndex = false;

        @Override
        public void onRender(Env env, Scope scope, Writer writer) {
            forIndex = getPara("forIndex", scope, false);
            super.onRender(env, scope, writer);
        }


        @Override
        protected String getUrl(int pageNumber) {
            HttpServletRequest request = JbootRequestContext.getRequest();
            String url = request.getRequestURI();
            String contextPath = JFinal.me().getContextPath();

            if (forIndex == false) {
                return Kits.doReplacePageNumber(url, pageNumber);
            }

            if (pageNumber == 1) {
                return contextPath + "/";
            } else if (url.equals(contextPath + "/")) {
                url = contextPath + "/article/category";
            }

            return Kits.doReplacePageNumber(url, pageNumber);
        }


        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("articlePage");
        }

    }
}
