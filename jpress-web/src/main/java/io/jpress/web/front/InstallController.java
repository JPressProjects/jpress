/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.front;

import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/install")
public class InstallController extends TemplateControllerBase {

    private static boolean installed = false;

    public static boolean isInstalled() {
        return installed;
    }

    public static void setInstalled(boolean installed) {
        InstallController.installed = installed;
    }

    public void index() {

        if (installed) {
            renderError(404);
            return;
        }

        render("/WEB-INF/views/commons/install.html");
    }


    public void doInstall() {
        if (installed) {
            renderError(404);
            return;
        }

        String username = getPara("username");
        String email = getPara("email");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtils.isBlank(username)) {
            renderJson(Ret.fail().set("message", "username must not be empty").set("errorCode", 1));
            return;
        }

        if (StrUtils.isBlank(email)) {
            renderJson(Ret.fail().set("message", "email must not be empty").set("errorCode", 2));
            return;
        }

        if (StrUtils.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "password must not be empty").set("errorCode", 3));
            return;
        }

        if (StrUtils.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "confirm password must not be empty").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "confirm password must equals password").set("errorCode", 5));
            return;
        }


    }


}
