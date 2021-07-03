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
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.model.UserCart;
import io.jpress.service.UserCartService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/userCart")
@Api("购物车相关API")
public class UserCartApiController extends ApiControllerBase {

    @Inject
    private UserCartService userCartService;

    @ApiOper("某个用户的购物车列表")
    public Ret listByUserId(@ApiPara("用户ID") @NotNull Long userId) {
        return Ret.ok().set("list", userCartService.findListByUserId(userId));
    }

    @ApiOper("删除购物车产品")
    public Ret doDelete(@ApiPara("购物车产品ID") @NotNull Long id) {
        userCartService.deleteById(id);
        return Rets.OK;
    }

    @ApiOper(value = "创建购物车产品",contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("购物车产品 JSON 信息") @JsonBody UserCart userCart) {
        Object id = userCartService.save(userCart);
        return Ret.ok().set("id", id);
    }

    @ApiOper(value = "更新购物车产品",contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("购物车产品 JSON 信息") @JsonBody UserCart userCart) {
        userCartService.update(userCart);
        return Rets.OK;
    }


}
