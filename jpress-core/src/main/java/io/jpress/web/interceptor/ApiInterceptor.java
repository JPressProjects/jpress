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

import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 * @Package io.jpress.web
 */
public class ApiInterceptor implements Interceptor {


    /**
     * api 的有效时间，默认为 10 分钟
     */
    private static final long TIMEOUT = 10 * 60 * 1000;


    @Inject
    private UserService userService;


    @Override
    public void intercept(Invocation inv) {

        boolean apiEnable = JPressOptions.getAsBool(JPressConsts.OPTION_API_ENABLE);
        String apiAppId = JPressOptions.get(JPressConsts.OPTION_API_APPID);
        String apiSecret = JPressOptions.get(JPressConsts.OPTION_API_SECRET);

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

        ApiControllerBase controller = (ApiControllerBase) inv.getController();
        String queryString = controller.getRequest().getQueryString();
        if (StrUtil.isBlank(queryString)) {
            inv.getController().renderJson(Ret.fail().set("message", "请求参数错误。"));
            return;
        }

        Map<String, String> parasMap = queryStringToMap(queryString);
        String appId = parasMap.get("jpressAppId");
        if (StrUtil.isBlank(appId)) {
            inv.getController().renderJson(Ret.fail().set("message", "在 Url 中未获取到 jpressAppId 内容，请注意 Url 是否正确。"));
            return;
        }


        if (!appId.equals(apiAppId)) {
            inv.getController().renderJson(Ret.fail().set("message", "客户端配置的AppId和服务端配置的不一致。"));
            return;
        }


        String timeStr = parasMap.get("ct");
        Long time = timeStr == null ? null : Long.valueOf(timeStr);
        if (time == null) {
            controller.renderJson(Ret.fail("message", "时间参数不能为空，请提交 ct 参数数据。"));
            return;
        }

        // 时间验证，可以防止重放攻击
        if (Math.abs(System.currentTimeMillis() - time) > TIMEOUT) {
            controller.renderJson(Ret.fail("message", "请求超时，请重新请求。"));
            return;
        }


        String sign = parasMap.get("sign");
        if (StrUtil.isBlank(sign)) {
            controller.renderJson(Ret.fail("message", "签名数据不能为空，请提交 sign 数据。"));
            return;
        }

        String localSign = createLocalSign(controller.getRequest(), apiSecret);
        if (!sign.equals(localSign)) {
            inv.getController().renderJson(Ret.fail().set("message", "数据签名错误。"));
            return;
        }

        Object userId = controller.getJwtPara(JPressConsts.JWT_USERID, false);
        if (userId != null) {
            controller.setAttr(JPressConsts.ATTR_LOGINED_USER, userService.findById(userId));
        }

        inv.invoke();
    }


    public static String createLocalSign(HttpServletRequest request, String apiSecret) {
        String queryString = request.getQueryString();
        Map<String, String> queryParas = StrUtil.queryStringToMap(queryString);
        String[] keys = queryParas.keySet().toArray(new String[0]);
        ;
        Arrays.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if ("sign".equals(key)) {
                continue;
            }

            String value = queryParas.get(key);
            sb.append(key).append(value);
        }

        return HashKit.md5(sb.append(apiSecret).toString());
    }


    private static Map<String, String> queryStringToMap(String queryString) {
        if (StrUtil.isBlank(queryString)) {
            return new HashMap<>();
        }

        Map<String, String> map = new HashMap<>();
        String[] params = queryString.split("&");
        for (String paramPair : params) {
            String[] keyAndValue = paramPair.split("=");
            if (keyAndValue.length == 2) {
                map.put(keyAndValue[0], StrUtil.urlDecode(keyAndValue[1]));
            } else if (keyAndValue.length == 1) {
                map.put(keyAndValue[0], "");
            }
        }
        return map;
    }


}
