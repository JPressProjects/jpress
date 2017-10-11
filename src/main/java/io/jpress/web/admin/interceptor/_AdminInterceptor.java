package io.jpress.web.admin.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StringUtils;
import io.jpress.Constants;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;

/**
 * 后台登录拦截器
 */
public class _AdminInterceptor implements Interceptor {

    @Inject
    UserService userService;

    @Override
    public void intercept(Invocation invocation) {
        String userId = EncryptCookieUtils.get(invocation.getController(), Constants.Cookies.USER_ID);
        if (StringUtils.isBlank(userId)) {
            invocation.getController().redirect("/admin/login");
            return;
        }

        User user = userService.findById(userId);
        if (user == null) {
            invocation.getController().redirect("/admin/login");
            return;
        }

        if (!user.isAdministrator()) {
            invocation.getController().redirect("/admin/login");
            return;
        }

        invocation.getController().setAttr("USER", user);
        invocation.invoke();
    }
}
