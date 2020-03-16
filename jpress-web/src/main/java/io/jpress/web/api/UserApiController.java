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
import io.jpress.model.User;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;

/**
 * 用户相关的API
 */
@RequestMapping("/api/user")
public class UserApiController extends ApiControllerBase {

    @Inject
    private UserService userService;

    /**
     * 获取用户信息
     */
    public void index() {

        Long id = getParaToLong("id");

        if (id == null) {
            renderFailJson();
            return;
        }

        User user = userService.findById(id);
        renderJson(Ret.ok().set("user", user));
    }


    /**
     * 获取登录用户自己的信息
     */
    public void me() {
        User user = getLoginedUser();
        if (user == null) {
            renderFailJson(1, "user not logined");
            return;
        }

        renderJson(Ret.ok().set("user", user));
    }


    public void save() {
        User user = getRawObject(User.class);

        if (user == null) {
            renderFailJson(1, "can not get user data");
            return;
        }

        user.keepUpdateSafe();
        userService.saveOrUpdate(user);

        renderOkJson();
    }
}
