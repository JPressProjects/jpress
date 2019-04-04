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
package io.jpress.module.route.controller;

import com.jfinal.aop.Aop;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import io.jpress.web.base.TemplateControllerBase;
import org.elasticsearch.common.inject.Inject;

/**
 * @author Eric.Huang 黄鑫（ninemm@126.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/route/search")
public class RouteSearchController extends TemplateControllerBase {

    @Inject
    TRouteService routeService;

    public void index() {

        String keyword = getEscapeHtmlPara("keyword");
/*        if (StrUtil.isBlank(keyword)) {
            renderError(404);
            return;
        }*/

        /**
         * 不让页面大于100，我认为：
         * 1、当一个真实用户在搜索某个关键字的内容，通过翻页去找对应数据，不可能翻到100页以上。
         * 2、翻页翻到100页以上，一般是机器：可能是来抓取数据的。
         */
        int page = getParaToInt("page", 1);
        if (page <= 0 || page > 100) {
            renderError(404);
            return;
        }

        String code = getPara("code");
        Long categoryId = getParaToLong("categoryId");

//        for test only
//        ArticleService articleService = Aop.get(ArticleService.class);
//        articleService.search(keyword,1,10);

        setAttr("keyword", keyword);
        setAttr("code", code);
        setAttr("categoryId", categoryId);
        // setAttr("page", page);

        setMenuActive(menu -> menu.isUrlStartWidth("/route/search"));

        render("artsearch.html");
    }

    public void paginate() {

        String keyword = getPara("keyword");
        String code = getPara("code");
        long categoryId = getParaToLong("categoryId", 10L);
        int page = getParaToInt("pageNum", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<TRoute> dataPage = Aop.get(TRouteService.class).search(keyword, code, categoryId, page, pageSize);
        renderJson(Ret.ok().set("page", dataPage));
    }

}
