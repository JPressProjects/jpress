/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/api/page")
@Api("页面相关的API")
public class PageApiController extends ApiControllerBase {

    @Inject
    private SinglePageService service;

    @ApiOper(value = "页面详情", paraNotes = "id 和 slug 必须有一个不能为空")
    public Ret detail(@ApiPara("页面ID") Long id, @ApiPara("页面固定连接") String slug) {
        if (id != null) {
            SinglePage page = service.findById(id);
            return Ret.ok("detail", page);
        }

        if (slug != null) {
            SinglePage page = service.findFirstBySlug(slug);
            return Ret.ok("detail", page);
        }

        return Rets.FAIL;
    }


    @ApiOper("根据 flag 查询页面列表")
    public Ret listByFlag(@ApiPara("页面的 flag 标识") @NotEmpty String flag) {
        List<SinglePage> pages = service.findListByFlag(flag);
        return Ret.ok().set("list", pages);
    }


    @ApiOper("删除页面")
    public Ret doDelete(@ApiPara("页面id") @NotNull Long id) {
        service.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建新页面", contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("页面 json 数据") @JsonBody SinglePage singlePage) {
        Object id = service.save(singlePage);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新页面", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("页面 json 数据") @JsonBody SinglePage singlePage) {
        service.update(singlePage);
        return Rets.OK;
    }


}
