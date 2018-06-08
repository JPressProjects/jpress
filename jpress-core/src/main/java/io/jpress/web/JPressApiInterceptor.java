package io.jpress.web;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 * @Package io.jpress.web
 */
public class JPressApiInterceptor implements Interceptor {
    public void intercept(Invocation inv) {
        inv.invoke();
    }
}
