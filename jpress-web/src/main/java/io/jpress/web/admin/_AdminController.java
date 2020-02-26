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
package io.jpress.web.admin;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.CaptchaValidate;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConfig;
import io.jpress.JPressConsts;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.model.User;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.interceptor.PermissionInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AdminController extends AdminControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Clear
    public void login() {

        if (!JPressHandler.getCurrentTarget().equals(JPressConfig.me.getAdminLoginPage())) {
            renderError(404);
            return;
        }

        setAttr("action", JPressConfig.me.getAdminLoginAction());
        render("login.html");
    }


    @Clear
    @EmptyValidate({
            @Form(name = "user", message = "账号不能为空"),
            @Form(name = "pwd", message = "密码不能为空"),
            @Form(name = "captcha", message = "验证码不能为空"),
    })
    @CaptchaValidate(form = "captcha",message = "验证码不正确，请重新输入")
    public void doLogin(String user, String pwd) {

        if (!JPressHandler.getCurrentTarget().equals(JPressConfig.me.getAdminLoginAction())) {
            renderError(404);
            return;
        }

        if (StrUtil.isBlank(user) || StrUtil.isBlank(pwd)) {
            throw new RuntimeException("你当前的编辑器（idea 或者 eclipse）可能有问题，请参考文档：http://www.jfinal.com/doc/3-3 进行配置");
        }

        User loginUser = userService.findByUsernameOrEmail(user);
        if (loginUser == null) {
            renderJson(Ret.fail("message", "用户名不正确。"));
            return;
        }

        if (!roleService.hasAnyRole(loginUser.getId())) {
            renderJson(Ret.fail("message", "您没有登录的权限。"));
            return;
        }

        Ret ret = userService.doValidateUserPwd(loginUser, pwd);

        if (ret.isOk()) {
            CookieUtil.put(this, JPressConsts.COOKIE_UID, loginUser.getId());
        }

        renderJson(ret);
    }

    // 必须清除PermissionInterceptor，
    // 防止在没有授权的情况下，用户无法退出的问题
    @Clear(PermissionInterceptor.class)
    public void logout() {
        CookieUtil.remove(this, JPressConsts.COOKIE_UID);
        redirect("/");
    }


    public void index() {
        List<String> moduleIncludes = new ArrayList<>();
        List<ModuleListener> listeners = ModuleManager.me().getListeners();
        for (ModuleListener listener : listeners) {
            String path = listener.onRenderDashboardBox(this);
            if (path == null) {
                continue;
            }

            if (path.startsWith("/")) {
                moduleIncludes.add(path);
            } else {
                moduleIncludes.add("/WEB-INF/views/admin/" + path);
            }
        }

        setAttr("moduleIncludes", moduleIncludes);
        render("index.html");
    }
}
