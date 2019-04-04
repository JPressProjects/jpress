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

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Eric.Huang 黄鑫 （ninemm@126.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/routes")
public class RoutesController extends TemplateControllerBase {

    @Inject
    private TRouteService routeService;

    public void paginate() {
        Long categoryId = getParaToLong("categoryId");
        String orderBy = getPara("orderBy");
        int pageNumber = getParaToInt("page", 1);

        Page<TRoute> page = categoryId == null
                ? routeService.paginateInNormal(pageNumber, 10, orderBy)
                : routeService.paginateByCategoryIdInNormal(pageNumber, 10, categoryId, orderBy);

        renderJson(Ret.ok().set("page", page));

    }
}
