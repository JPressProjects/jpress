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
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.service.UserService;

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

    /**
     * api 的有效时间，默认为 10 分钟
     */
    private static final long timeout = 10 * 60 * 1000;

    public ApiInterceptor() {
        JPressOptions.addListener(this);
    }


    public static void init() {
        apiEnable = JPressOptions.getAsBool(JPressConsts.OPTION_API_ENABLE);
        apiAppId = JPressOptions.get(JPressConsts.OPTION_API_APPID);
        apiSecret = JPressOptions.get(JPressConsts.OPTION_API_SECRET);
    }

    @Inject
    private UserService userService;

    @Override
    public void intercept(Invocation inv) {

        // API 功能未启用
        if (apiEnable == false) {
            inv.getController().renderJson(Ret.fail().set("message", "API功能已经关闭，请管理员在后台进行开启"));
            return;
        }

        if (StrUtil.isBlank(apiAppId)) {
            inv.getController().renderJson(Ret.fail().set("message", "后台配置的 APP ID 不能为空，请先进入后台的接口管理进行配置。"));
            return;
        }

        if (StrUtil.isBlank(apiSecret)) {
            inv.getController().renderJson(Ret.fail().set("message", "后台配置的 API 密钥不能为空，请先进入后台的接口管理进行配置。"));
            return;
        }


        JbootController controller = (JbootController) inv.getController();
        String appId = controller.getPara("appId");
        if (StrUtil.isBlank(appId)) {
            inv.getController().renderJson(Ret.fail().set("message", "在Url中获取到appId内容，请注意Url是否正确。"));
            return;
        }


        if (!appId.equals(apiAppId)) {
            inv.getController().renderJson(Ret.fail().set("message", "客户端配置的AppId和服务端配置的不一致。"));
            return;
        }

        String sign = controller.getPara("sign");
        if (StrUtil.isBlank(sign)) {
            controller.renderJson(Ret.fail("message", "签名数据不能为空，请提交 sign 数据。"));
            return;
        }

        Long time = controller.getParaToLong("t");
        if (time == null) {
            controller.renderJson(Ret.fail("message", "时间参数不能为空，请提交 t 参数数据。"));
            return;
        }

        // 时间验证，可以防止重放攻击
        if (Math.abs(System.currentTimeMillis() - time) > timeout) {
            controller.renderJson(Ret.fail("message", "请求超时，请重新请求。"));
            return;
        }

        String localSign = createLocalSign(controller);
        if (sign.equals(localSign) == false) {
            inv.getController().renderJson(Ret.fail().set("message", "数据签名错误。"));
            return;
        }

        Object userId = controller.getJwtPara(JPressConsts.JWT_USERID);
        if (userId != null) {
            controller.setAttr(JPressConsts.ATTR_LOGINED_USER, userService.findById(userId));
        }

        inv.invoke();
    }

    private String createLocalSign(Controller controller) {

        String queryString = controller.getRequest().getQueryString();
        Map<String, String[]> params = controller.getRequest().getParameterMap();

        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            if ("sign".equals(key)) {
                continue;
            }

            //只对get参数里的进行签名
            if (queryString.indexOf(key + "=") == -1) {
                continue;
            }

            String value = params.get(key)[0];
            query.append(key).append(value);
        }
        query.append(apiSecret);
        return HashKit.md5(query.toString());
    }


    @Override
    public void onChanged(String key, String newValue, String oldValue) {

        switch (key) {
            case JPressConsts.OPTION_API_ENABLE:
                apiEnable = JPressOptions.getAsBool(JPressConsts.OPTION_API_ENABLE);
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
