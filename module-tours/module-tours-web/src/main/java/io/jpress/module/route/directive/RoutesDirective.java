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
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;

import java.util.List;

/**
 * @author Eric Huang 黄鑫 （ninemm@126.com）
 * @version V1.0
 * @Package io.jpress.module.route.directive
 */
@JFinalDirective("routes")
public class RoutesDirective extends JbootDirectiveBase {

    @Inject
    private TRouteService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String code = getPara("code", scope);
        String style = getPara("style", scope);
        String title = getPara("title", scope);
        String isTop = getPara("isTop", scope);

        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "id desc");
        int count = getParaToInt("count", scope, 10);

        Columns columns = Columns.create("code", code);
        columns.add("style", style);
        columns.add("is_top", isTop);
        columns.likeAppendPercent("title", title);
        columns.add("status", TRoute.STATUS_NORMAL);

        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.is_not_null("thumbnail");
            } else {
                columns.is_null("thumbnail");
            }
        }

        List<TRoute> routes = service.findListByColumns(columns, orderBy, count);

        if (routes == null || routes.isEmpty()) {
            return;
        }

        scope.setLocal("routes", routes);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
