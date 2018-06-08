package io.jpress.web;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class JPressAdminInterceptor implements Interceptor {
    public void intercept(Invocation inv) {
        inv.invoke();
    }
}
