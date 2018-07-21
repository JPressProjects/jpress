package io.jpress.core.web.base;

import com.google.inject.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StringUtils;
import io.jpress.model.User;
import io.jpress.service.UserService;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 * @Package io.jpress.web
 */
public class ApiInterceptor implements Interceptor {

    @Inject
    private UserService userService;

    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        String sign = controller.getPara("sign");
        int userId = controller.getParaToInt("userId", 0);

        if (StringUtils.isBlank(sign)) {
            controller.renderJson(Ret.fail("message", "sign is blank"));
            return;
        }

        if (userId == 0) {
            controller.renderJson(Ret.fail("message", "userId is error"));
            return;
        }

        User user = userService.findById(userId);
        if (user == null) {
            controller.renderJson(Ret.fail("message", "userId is error"));
            return;
        }


        inv.invoke();
    }


    public static String signCheak(String accessKey, Map<String, String[]> params) {
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
                if (value != null && StringUtils.areNotBlank(key, value.toString())) {
                    query.append(key).append(value);
                }
            }
        }
        query.append(accessKey);
        return query.toString();
    }
}
