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
import io.jpress.model.UserAddress;
import io.jpress.service.UserAddressService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/userAddress")
@Api("用户地址相关API")
public class UserAddressApiController extends ApiControllerBase {


    @Inject
    private UserAddressService userAddressService;


    @ApiOper("收货地址详情")
    public Ret detail(@ApiPara("收货地址ID") @NotNull Long id) {
        return Ret.ok().set("detail", userAddressService.findById(id));
    }


    @ApiOper("某个用户的收货地址列表")
    public Ret listByUserId(@ApiPara("用户ID") @NotNull Long userId) {
        List<UserAddress> addresses = userAddressService.findListByUserId(userId);
        return Ret.ok().set("list", addresses);
    }


    @ApiOper("获取用户默认的收货地址")
    public Ret findUserDefaultAddress(@ApiPara("用户ID") @NotNull Long userId) {
        return Ret.ok("address", userAddressService.findDefaultAddress(userId));
    }


    @ApiOper("删除用户地址")
    public Ret doDelete(@ApiPara("收货地址ID") @NotNull Long id) {
        userAddressService.deleteById(id);
        return Rets.OK;
    }

    @ApiOper(value = "新增用户地址", contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("收货地址ID") @JsonBody UserAddress userAddress) {
        Object id = userAddressService.save(userAddress);
        return Ret.ok().set("id", id);
    }

    @ApiOper(value = "更新用户地址", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("收货地址ID") @JsonBody UserAddress userAddress) {
        userAddressService.update(userAddress);
        return Rets.OK;
    }


}
