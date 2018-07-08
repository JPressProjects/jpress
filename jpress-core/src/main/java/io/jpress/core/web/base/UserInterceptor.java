package io.jpress.core.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.EncryptCookieUtils;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户信息的拦截器
 * @Package io.jpress.web
 */
public class UserInterceptor implements Interceptor {

    @Inject
    private UserService userService;

    private static final ThreadLocal<User> threadLocal = new ThreadLocal<User>();
    public static User getThreadLocalUser() {
        return threadLocal.get();
    }


    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String userid = EncryptCookieUtils.get(controller, "jp_user");

        if (userid == null || userid.trim().length() == 0) {
            inv.invoke();
            return;
        }

        User user = userService.findById(userid);

        try {
            threadLocal.set(user);
            inv.invoke();
        } finally {
            threadLocal.remove();
        }
    }
}
