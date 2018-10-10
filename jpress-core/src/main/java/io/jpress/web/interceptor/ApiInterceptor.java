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
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.annotation.NeedAuthentication;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 * @Package io.jpress.web
 */
public class ApiInterceptor implements Interceptor, JPressOptions.OptionChangeListener {

    private static boolean apiEnable = false;

    private static String apiAppId = null;
    private static String apiSecret = null;

    public ApiInterceptor() {
        JPressOptions.addListener(this);
    }


    public static void init() {
        apiEnable = JPressOptions.getAsBool(JPressConsts.OPTION_API_ENABLE);
        apiAppId = JPressOptions.get(JPressConsts.OPTION_API_APPID);
        apiSecret = JPressOptions.get(JPressConsts.OPTION_API_SECRET);
    }


    public void intercept(Invocation inv) {

        String target = inv.getActionKey();
        if (!target.startsWith("/api/")) {
            inv.invoke();
            return;
        }


        if (apiEnable == false) {
            inv.getController().renderJson(Ret.fail().set("message", "api closed."));
            return;
        }


        if (inv.getMethod().getDeclaredAnnotation(NeedAuthentication.class) == null) {
            inv.invoke();
            return;
        }


        if (StrUtils.isBlank(apiSecret)) {
            inv.getController().renderJson(Ret.fail().set("message", "config error"));
            return;
        }


        Controller controller = inv.getController();

        String appId = controller.getPara("appid");
        if (StrUtils.isBlank(appId)) {
            inv.getController().renderJson(Ret.fail().set("message", "apiId is error"));
            return;
        }


        if (!appId.equals(apiAppId)) {
            inv.getController().renderJson(Ret.fail().set("message", "apiId is error"));
            return;
        }

        String sign = controller.getPara("sign");

        if (StrUtils.isBlank(sign)) {
            controller.renderJson(Ret.fail("message", "sign is blank"));
            return;
        }

        String localSing = createLocalSign(inv.getController().getRequest().getParameterMap());
        if (sign.equals(localSing) == false) {
            inv.getController().renderJson(Ret.fail().set("message", "sign error"));
            return;
        }

        inv.invoke();
    }

    private String createLocalSign(Map<String, String[]> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            if (ArrayUtils.isNullOrEmpty(params.get(key))) {
                continue;
            }
            if ("sign".equals(key)) {
                continue;
            }
            for (String value : params.get(key)) {
                if (value != null && StrUtils.notBlank(key, value.toString())) {
                    query.append(key).append(value);
                }
            }
        }
        query.append(apiSecret);
        return query.toString();
    }

    @Override
    public void onChanged(String key, String newValue, String oldValue) {

        switch (key) {
            case JPressConsts.OPTION_API_ENABLE:
                apiEnable = Boolean.parseBoolean(newValue);
                break;
            case JPressConsts.OPTION_API_APPID:
                apiAppId = newValue;
                break;
            case JPressConsts.OPTION_API_SECRET:
                apiSecret = newValue;
                break;
        }
    }
}
