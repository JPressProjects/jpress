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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import io.jboot.utils.RequestUtil;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 要求用户必须登陆，拦截器必须放在 UserInterceptor 之后执行
 */
public class UserMustLoginedInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        if (UserInterceptor.getThreadLocalUser() == null) {
            if (RequestUtil.isAjaxRequest(inv.getController().getRequest())) {
                inv.getController().renderJson(Ret.fail("message", "用户未登录"));
            } else {
                inv.getController().redirect("/user/login");
            }
            return;
        }

        inv.invoke();

    }

}
