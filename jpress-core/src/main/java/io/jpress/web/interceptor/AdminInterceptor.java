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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConsts;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.SystemMenuManager;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class AdminInterceptor implements Interceptor {


    @Inject
    private UserService us;


    public void intercept(Invocation inv) {


        String uid = EncryptCookieUtils.get(inv.getController(), JPressConsts.COOKIE_UID);
        if (StrUtils.isBlank(uid)) {
            inv.getController().redirect("/admin/login");
            return;
        }

        User user = us.findById(uid);
        if (user == null || !user.isStatusOk()) {
            inv.getController().redirect("/admin/login");
            return;
        }

        List<MenuGroup> systemMenuGroups = SystemMenuManager.me().getSystemMenus();
        List<MenuGroup> moduleMenuGroups = SystemMenuManager.me().getModuleMenus();

        inv.getController().setAttr("systemMenuGroups", systemMenuGroups);
        inv.getController().setAttr("moduleMenuGroups", moduleMenuGroups);

        inv.getController().setAttr(JPressConsts.ATTR_LOGINED_USER, user);

        inv.invoke();
    }

}
