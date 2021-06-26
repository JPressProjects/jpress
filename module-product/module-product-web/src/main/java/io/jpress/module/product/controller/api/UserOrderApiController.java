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

import com.jfinal.kit.Ret;
import io.jboot.aop.annotation.DefaultValue;
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
public class UserOrderApiController extends ApiControllerBase {


    private UserOrderService userOrderService;

    /**
     * 购物车列表
     */
    public Ret paginateByUserId(@DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize, @NotNull Long userId, String title, String ns) {
        return Ret.ok().set("page", userOrderService.paginateByUserId(pageNumber, pageSize, userId, title, ns));
    }

    /**
     * 购物车中删除
     */
    public Ret doDelete(@NotNull Long id) {
        userOrderService.deleteById(id);
        return Rets.OK;
    }

    /**
     * 添加到购物车
     */
    public Ret doCreate(@JsonBody UserOrder userOrder) {
        Object id = userOrderService.save(userOrder);
        return Ret.ok().set("id",id);
    }

    /**
     * 改变购物车中商品的数量
     */
    public Ret doUpdate(@JsonBody UserOrder userOrder) {
        userOrderService.update(userOrder);
        return Rets.OK;
    }


}
