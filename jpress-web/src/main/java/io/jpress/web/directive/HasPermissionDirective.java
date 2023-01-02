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
package io.jpress.web.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.User;
import io.jpress.service.PermissionService;
import io.jpress.web.interceptor.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("hasPermission")
public class HasPermissionDirective extends JbootDirectiveBase {

    @Inject
    private PermissionService permissionService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null || !user.isStatusOk()) {
            return;
        }


        String permission = getPara(0, scope);
        if (permission == null || permission.trim().length() == 0) {
            throw new IllegalArgumentException("#hasPermission(...) argument must not be empty," + getLocation());
        }

        if (permissionService.hasPermission(user.getId(), permission)) {
            renderBody(env, scope, writer);
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

