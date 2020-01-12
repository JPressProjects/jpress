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
import com.jfinal.core.Controller;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 微信获取用户相关的拦截器
 */

public class WechatInterceptor implements Interceptor, JPressOptions.OptionChangeListener {

    /**
     * 是否启用微信拦截
     */
    private static boolean enable = false;

    public static void init() {
        enable = JPressOptions.getAsBool(JPressConsts.OPTION_WECHAT_WEB_AUTHORIZE_ENABLE);
    }

    public WechatInterceptor() {
        JPressOptions.addListener(this);
    }


    @Override
    public void onChanged(String key, String newValue, String oldValue) {

        if (JPressConsts.OPTION_WECHAT_WEB_AUTHORIZE_ENABLE.equals(key)) {
            init();
        }
    }

    @Override
    public void intercept(Invocation inv) {
        if (enable == false) {
            inv.invoke();
            return;
        }


        String uid = CookieUtil.get(inv.getController(), JPressConsts.COOKIE_UID);
        if (StrUtil.isNotBlank(uid)) {
            inv.invoke();
            return;
        }

        if (RequestUtil.isWechatBrowser(inv.getController().getRequest()) == false) {
            inv.invoke();
            return;
        }

        String gotoUrl = getGotoUrl(inv.getController());
        inv.getController().redirect("/wechat/authorization?goto=" + gotoUrl);
    }

    /**
     * 获取当前的url
     *
     * @param controller
     * @return
     */
    public static String getGotoUrl(Controller controller) {

        HttpServletRequest req = controller.getRequest();

        // 获取用户将要去的路径
        String queryString = req.getQueryString();

        // 被拦截前的请求URL
        String url = req.getScheme() + "://" + req.getServerName() + req.getRequestURI();
        if (StrUtil.isNotBlank(queryString)) {
            url = url.concat("?").concat(queryString);
        }

        return StrUtil.urlEncode(url);
    }
}
