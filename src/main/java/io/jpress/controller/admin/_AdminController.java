/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.controller.admin;

import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.Constants;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;

@RequestMapping(value = "/admin", viewPath = "/WEB-INF/admin")
public class _AdminController extends JbootController {

    @Inject
    UserService userService;

    public void login(String username, String password) {
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            render("login.html");
            return;
        }

        Ret ret = userService.login(username, password);
        if (ret.isOk()) {
            User user = ret.getAs("user");
            Jboot.me().sendEvent(Constants.Actions.USER_LOGINED, user);
            EncryptCookieUtils.put(this, Constants.Cookies.USER_ID, user.getId());
        }

        renderText(ret.toJson());
    }

    public void logout() {
        EncryptCookieUtils.remove(this, Constants.Cookies.USER_ID);
        redirect("/admin/login");
    }

}
