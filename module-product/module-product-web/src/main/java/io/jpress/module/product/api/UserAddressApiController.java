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
package io.jpress.module.product.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.model.UserAddress;
import io.jpress.service.UserAddressService;
import io.jpress.web.base.ApiControllerBase;

import java.util.List;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/userAddress")
public class UserAddressApiController extends ApiControllerBase {


    @Inject
    private UserAddressService userAddressService;

    /**
     * 收货地址列表
     */
    public void index() {
        List<UserAddress> addresses = userAddressService.findListByUserId(getLoginedUser().getId());
        renderOkJson("data", addresses);
    }

    /**
     * 删除收货地址
     */
    @EmptyValidate({@Form(name = "id", message = "收货地址ID不能为空")})
    public void doDelUserAddress(Long id) {
        UserAddress address = userAddressService.findById(id);
        if (isLoginedUserModel(address)) {
            userAddressService.delete(address);
        }
        renderOkJson();
    }

    /**
     * 添加收货地址
     */
    @EmptyValidate({
            @Form(name = "address.username", message = "请填写联系人"),
            @Form(name = "address.mobile", message = "请填写联系方式"),
            @Form(name = "address.detail", message = "请填写联系地址"),
    })
    public void doAddUserAddress() {
        UserAddress address = getBean(UserAddress.class, "address");
        userAddressService.addUserAddress(address, getLoginedUser().getId());
        renderJson(Ret.ok());
    }


    /**
     * 获取用户默认的收货地址
     */
    public void findDefaultAddress() {
        renderOkJson("data", userAddressService.findDefaultAddress(getLoginedUser().getId()));
    }


}
