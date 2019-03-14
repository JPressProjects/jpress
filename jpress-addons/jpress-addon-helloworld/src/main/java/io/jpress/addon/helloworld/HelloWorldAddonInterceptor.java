package io.jpress.addon.helloworld;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;


public class HelloWorldAddonInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        System.out.println("HelloWorldAddonInterceptor invoke");

        inv.invoke();
    }
}
