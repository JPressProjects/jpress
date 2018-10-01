package io.jpress.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 要求用户必须登陆，拦截器必须放在 UserInterceptor 之后执行
 * @Package io.jpress.web
 */
public class UserMustLoginedInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        if (UserInterceptor.getThreadLocalUser() == null) {
            inv.getController().redirect("/user/login");
            return;
        }

        inv.invoke();

    }

}
