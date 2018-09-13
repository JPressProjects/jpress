package io.jpress.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StringUtils;
import io.jpress.JPressConstants;
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

        User user = controller.getAttr(JPressConstants.ATTR_LOGINED_USER);
        if (user != null) {
            doInvoke(inv, user);
            return;
        }

        String uid = EncryptCookieUtils.get(inv.getController(), JPressConstants.COOKIE_UID);

        if (StringUtils.isNotBlank(uid)) {
            doInvoke(inv, userService.findById(uid));
            return;
        }

        inv.invoke();
    }

    private void doInvoke(Invocation inv, User user) {

        if (user == null) {
            inv.invoke();
            return;
        }

        threadLocal.set(user);
        inv.invoke();
    }
}
