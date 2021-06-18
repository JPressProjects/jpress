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
package io.jpress.web.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;

/**
 * 用户相关的API
 */
@RequestMapping("/api/user")
public class UserApiController extends ApiControllerBase {

    @Inject
    private UserService userService;


    /**
     * 用户登录
     */
    public Ret login(@NotNull String loginAccount, @NotNull  String password) {
       return Ret.ok();
    }


    /**
     * 获取用户信息
     */
    public Ret query(@NotNull Long id) {
        User user = userService.findById(id);
        return Ret.ok().set("user",user);
    }


    /**
     * 更新用户信息
     * @return
     */
    public Ret update(@JsonBody @NotNull  User user) {
        user.keepUpdateSafe();
        userService.saveOrUpdate(user);
        return Ret.ok();
    }


    /**
     * 创新新用户
     * @return
     */
    public Ret create(@JsonBody @NotNull  User user) {
        user.keepUpdateSafe();
        userService.save(user);
        return Ret.ok();
    }
}
