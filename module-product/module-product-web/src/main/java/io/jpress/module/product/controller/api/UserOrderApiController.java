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
package io.jpress.module.product.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.model.UserOrder;
import io.jpress.service.UserOrderService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/userOrder")
@Api("订单相关API")
public class UserOrderApiController extends ApiControllerBase {


    @Inject
    private UserOrderService userOrderService;


    @ApiOper("分页查询用户订单")
    public Ret paginateByUserId(@ApiPara("用户ID") @NotNull Long userId
            , @ApiPara(value = "订单标题", notes = "一般用户搜索") String title
            , @ApiPara(value = "订单号", notes = "一般用户搜索") String ns
            , @ApiPara("页码") @DefaultValue("1") int pageNumber
            , @ApiPara("每页数据量") @DefaultValue("10") int pageSize) {
        return Ret.ok().set("page", userOrderService.paginateByUserId(pageNumber, pageSize, userId, title, ns));
    }


    @ApiOper("删除订单")
    public Ret doDelete(@ApiPara("订单ID") @NotNull Long id) {
        userOrderService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建订单", contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("订单的 JSON 信息") @NotNull @JsonBody UserOrder userOrder) {
        Object id = userOrderService.save(userOrder);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新订单", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("订单的 JSON 信息") @NotNull @JsonBody UserOrder userOrder) {
        userOrderService.update(userOrder);
        return Rets.OK;
    }


}
