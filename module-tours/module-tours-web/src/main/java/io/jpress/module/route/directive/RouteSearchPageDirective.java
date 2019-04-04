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
package io.jpress.module.route.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.JbootPaginateDirective;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;

/**
 * @author Eric.Huang 黄鑫 （ninemm@126.com）
 * @version V1.0
 * @Package io.jpress.module.route.directive
 */
@JFinalDirective("routeSearchPage")
public class RouteSearchPageDirective extends JbootDirectiveBase {

    @Inject
    private TRouteService routeService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        String keyword = controller.getAttr("keyword");
/*        if (StrUtil.isBlank(keyword)) {
            return;
        }*/
        String code = controller.getAttr("code");
        long categoryId = controller.getAttr("categoryId");
        int page = controller.getAttr("page");
        int pageSize = getParaToInt("pageSize", scope, 10);

        Page<TRoute> dataPage = routeService.search(keyword, code, categoryId, page, pageSize);
        if (dataPage != null) {
            scope.setGlobal("routePage", dataPage);
            renderBody(env, scope, writer);
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("routeSearchPaginate")
    public static class SearchPaginateDirective extends JbootPaginateDirective {
        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("routePage");
        }

    }
}
