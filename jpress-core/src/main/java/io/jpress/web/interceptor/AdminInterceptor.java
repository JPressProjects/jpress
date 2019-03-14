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
package io.jpress.web.interceptor;

import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressApplicationConfig;
import io.jpress.JPressConsts;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuManager;
import io.jpress.model.User;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.handler.JPressHandler;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class AdminInterceptor implements Interceptor {


    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private JPressApplicationConfig config;


    public void intercept(Invocation inv) {

        if (JPressHandler.getCurrentTarget().equals(config.getAdminLoginPage())) {
            inv.getController().forwardAction("/admin/login");
            return;
        }

        if (JPressHandler.getCurrentTarget().equals(config.getAdminLoginAction())) {
            inv.getController().forwardAction("/admin/doLogin");
            return;
        }

        String uid = CookieUtil.get(inv.getController(), JPressConsts.COOKIE_UID);
        if (StrUtil.isBlank(uid)) {
            inv.getController().renderError(404);
            return;
        }

        User user = userService.findById(uid);
        if (user == null || !user.isStatusOk()) {
            inv.getController().renderError(404);
            return;
        }

        //不允许没有任何权限的用户访问后台
        if (!roleService.hasAnyRole(user.getId())) {
            inv.getController().renderError(404);
            return;
        }

        List<MenuGroup> systemMenuGroups = MenuManager.me().getSystemMenus();
        List<MenuGroup> moduleMenuGroups = MenuManager.me().getModuleMenus();

        inv.getController().setAttr("systemMenuGroups", systemMenuGroups);
        inv.getController().setAttr("moduleMenuGroups", moduleMenuGroups);

        inv.getController().setAttr(JPressConsts.ATTR_LOGINED_USER, user);

        inv.invoke();
    }


}
