package io.jpress.addon.helloworld;


import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HelloWorldAddonHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        System.out.println("HelloWorldAddonHandler invoked for target : " + target);
        next.handle(target, request, response, isHandled);
    }
}
