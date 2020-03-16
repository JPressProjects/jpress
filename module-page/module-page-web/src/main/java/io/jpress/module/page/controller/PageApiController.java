/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.page.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.ApiControllerBase;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/api/page")
public class PageApiController extends ApiControllerBase {

    @Inject
    private SinglePageService service;

    public void index() {
        Long id = getParaToLong("id");
        if (id != null) {
            SinglePage page = service.findById(id);
            renderJson(Ret.ok("page", page));
            return;
        }

        String slug = getPara("slug");
        if (slug != null) {
            SinglePage page = service.findFirstBySlug(slug);
            renderJson(Ret.ok("page", page));
            return;
        }

        renderFailJson();
    }

    public void list() {
        String flag = getPara("flag");
        List<SinglePage> pages = service.findListByFlag(flag);
        renderOkJson("pages", pages);
    }


}
