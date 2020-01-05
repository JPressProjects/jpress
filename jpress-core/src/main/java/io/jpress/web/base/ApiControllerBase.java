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
package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import io.jpress.web.interceptor.ApiInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({ApiInterceptor.class, UserInterceptor.class})
public abstract class ApiControllerBase extends ControllerBase {

    protected void renderFailJson(String message) {
        renderJson(Ret.fail("message", message));
    }

    protected void renderFailJson(int code, String message) {
        renderJson(Ret.fail("code", code).set("message", message));
    }

    protected void renderOkJson(String attr, Object value) {
        renderJson(Ret.ok(attr, value));
    }
    protected void renderOkDataJson(Object value){
        renderJson(Ret.ok("data", value));
    }


}
