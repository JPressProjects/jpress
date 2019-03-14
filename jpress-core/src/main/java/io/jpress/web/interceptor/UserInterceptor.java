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
import com.jfinal.core.Controller;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.JPressConsts;
import io.jpress.model.User;
import io.jpress.service.UserService;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户信息的拦截器
 * @Package io.jpress.web
 */
public class UserInterceptor implements Interceptor {

    @Inject
    private UserService userService;

    public static User getThreadLocalUser() {
        return JbootControllerContext.get().getAttr(JPressConsts.ATTR_LOGINED_USER);
    }

    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();
        User user = controller.getAttr(JPressConsts.ATTR_LOGINED_USER);

        if (user != null) {
            inv.invoke();
            return;
        }


        String uid = CookieUtil.get(inv.getController(), JPressConsts.COOKIE_UID);
        if (StrUtil.isBlank(uid)) {
            inv.invoke();
            return;
        }

        user = userService.findById(uid);
        if (user != null) {
            inv.getController().setAttr(JPressConsts.ATTR_LOGINED_USER, user);
        }

        inv.invoke();
    }

}
