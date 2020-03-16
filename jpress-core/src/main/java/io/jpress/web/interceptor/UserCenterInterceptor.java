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
package io.jpress.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuManager;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户中心的拦截器，用户中心要求用户必须登录
 * 该拦截器应该放在 UserInterceptor 之后执行
 */
public class UserCenterInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        List<MenuGroup> ucenterMenus = MenuManager.me().getUcenterMenus();
        inv.getController().setAttr("ucenterMenus", ucenterMenus);
        inv.getController().setAttr("user", UserInterceptor.getThreadLocalUser());

        inv.invoke();
    }

}
