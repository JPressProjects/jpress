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
package io.jpress.web.admin;

import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.interceptor.PermissionInterceptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin")
public class _AdminController extends AdminControllerBase {

    @Inject
    private UserService us;

    @Clear
    public void login() {
        render("login.html");
    }


    @Clear
    @EmptyValidate({
            @Form(name = "user", message = "账号不能为空"),
            @Form(name = "pwd", message = "密码不能为空")
    })
    public void doLogin(String user, String pwd) {

        if (StrUtils.isBlank(user) || StrUtils.isBlank(pwd)) {
            throw new RuntimeException("你当前的编辑器（idea 或者 eclipse）可能有问题，请参考文档：http://www.jfinal.com/doc/3-3 进行配置");
        }

        Ret ret = StrUtils.isEmail(user)
                ? us.loginByEmail(user, pwd)
                : us.loginByUsername(user, pwd);

        if (ret.isOk()) {
            EncryptCookieUtils.put(this, JPressConsts.COOKIE_UID, ret.getLong("user_id"));
        }

        renderJson(ret);
    }

    //清除PermissionInterceptor，防止在没有授权的情况下，用户无法退出的问题
    @Clear(PermissionInterceptor.class)
    public void doLogout() {
        EncryptCookieUtils.remove(this, JPressConsts.COOKIE_UID);
        redirect("/admin/login");
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
