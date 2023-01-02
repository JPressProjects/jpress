/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.kit.Ret;
import io.jboot.utils.RequestUtil;
import io.jpress.model.User;
import io.jpress.service.PermissionService;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 */
public class PermissionInterceptor implements Interceptor {


    private static final String NO_PERMISSION_VIEW = "/WEB-INF/views/admin/error/nopermission.html";

    @Inject
    private PermissionService permissionService;

    @Override
    public void intercept(Invocation inv) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            render(inv);
            return;
        }

        if (!permissionService.hasPermission(user.getId(), inv.getActionKey())) {
            render(inv);
            return;
        }

        inv.invoke();
    }

    public void render(Invocation inv) {
        if (RequestUtil.isAjaxRequest(inv.getController().getRequest())) {
            inv.getController().renderJson(Ret.fail().set("message", "您没有权限操作此功能。"));
        } else {
            inv.getController().render(NO_PERMISSION_VIEW);
        }
    }
}
