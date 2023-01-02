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
import com.jfinal.core.Controller;
import io.jpress.JPressOptions;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 */
public class JPressInterceptor implements Interceptor {

    public static final String ADDON_PATH_KEY = "APATH";

    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        //方便模板开发者直接在模板里接收参数
        controller.setAttr("C", controller);
        controller.setAttr("CDN", JPressOptions.getCDNDomain());
        controller.setAttr(ADDON_PATH_KEY, "");

        inv.invoke();
    }


}
