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
import com.jfinal.kit.Ret;
import com.jfinal.render.TextRender;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于预防 csrf 攻击的拦截器
 * @Package io.jpress.web
 * <p>
 * 备注：此拦截器只对 do 开头的方法进行拦截。例如：127.0.0.1/user/doDel?id=xxx
 */
public class CSRFInterceptor implements Interceptor {

    public static final String CSRF_ATTR_KEY = "CSRF_TOKEN";
    public static final String CSRF_KEY = "csrf_token";


    public void intercept(Invocation inv) {

        //不是 do 开头的，让其通过
        //在JPress里有一个共识：只要是 增、删、改的操作，都会用do开头对方法进行命名
        if (inv.getMethodName().startsWith("do") == false) {
            renderNormal(inv);
            return;
        }

        //从cookie中读取token，因为 第三方网站 无法修改 和 获得 cookie
        //所以从cookie获取存储的token是安全的
        String cookieToken = inv.getController().getCookie(CSRF_KEY);
        if (StrUtil.isBlank(cookieToken)) {
            renderBad(inv);
            return;
        }

        //url参数里的csrf_token
        String paraToken = inv.getController().getPara(CSRF_KEY);
        if (StrUtil.isBlank(paraToken)) {
            renderBad(inv);
            return;
        }

        if (cookieToken.equals(paraToken) == false) {
            renderBad(inv);
            return;
        }

        renderNormal(inv);
    }


    private void renderNormal(Invocation inv) {
        // 不是 ajax 请求，才需要重置本地 的token
        // ajax 请求，需要保证之前的token可以继续使用
        if (RequestUtil.isAjaxRequest(inv.getController().getRequest()) == false) {
            String uuid = StrUtil.uuid();
            inv.getController().setCookie(CSRF_KEY, uuid, -1);
            inv.getController().setAttr(CSRF_ATTR_KEY, uuid);
        }

        inv.invoke();
    }


    private void renderBad(Invocation inv) {
        if (RequestUtil.isAjaxRequest(inv.getController().getRequest())) {
            inv.getController().renderJson(Ret.fail().set("message", "bad or mission token!"));
        } else {
            inv.getController().renderError(403, new TextRender("bad or missing token!"));
        }
    }

}
