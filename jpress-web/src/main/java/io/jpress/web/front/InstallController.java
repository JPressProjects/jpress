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

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.model.User;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

import javax.inject.Inject;
import java.util.Date;

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


    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private OptionService optionService;

    public void index() {

        //已经安装了，不让进行访问
        if (installed == true) {
            renderError(404);
            return;
        }

        render("/WEB-INF/views/commons/install.html");
    }


    public void doInstall() {

        //已经安装了，不让进行访问
        if (installed == true) {
            renderError(404);
            return;
        }

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        String webName = getPara("web_name");
        String webTitle = getPara("web_title");
        String webSubtitle = getPara("web_subtitle");

        if (StrUtils.isBlank(username)) {
            renderJson(Ret.fail().set("message", "账号不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtils.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtils.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        if (StrUtils.isBlank(webName)) {
            renderJson(Ret.fail().set("message", "网站名称不能为空").set("errorCode", 10));
            return;
        }

        if (StrUtils.isBlank(webTitle)) {
            renderJson(Ret.fail().set("message", "网站标题不能为空").set("errorCode", 11));
            return;
        }

        if (StrUtils.isBlank(webSubtitle)) {
            renderJson(Ret.fail().set("message", "网站副标题不能为空").set("errorCode", 12));
            return;
        }

        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        User user = new User();
        user.setId(1l);
        user.setUsername(username);
        user.setNickname(username);
        user.setRealname(username);
        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());
        user.setActivated(new Date());
        user.setStatus(User.STATUS_OK);
        user.setCreateSource("web_register");

        userService.save(user);
        roleService.initWebRole();

        optionService.saveOrUpdate("web_name", webName);
        optionService.saveOrUpdate("web_title", webTitle);
        optionService.saveOrUpdate("web_subtitle", webSubtitle);

        JPressOptions.set("web_name", webName);
        JPressOptions.set("web_title", webTitle);
        JPressOptions.set("web_subtitle", webSubtitle);

        setInstalled(true);
        renderJson(Ret.ok());
    }


}
