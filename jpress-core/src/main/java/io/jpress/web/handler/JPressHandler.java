package io.jpress.web.handler;


import com.jfinal.handler.Handler;
import io.jboot.utils.StrUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 伪静态处理器
 * @Package io.jpress.web.handler
 */

public class JPressHandler extends Handler {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static String getCurrentTarget() {
        return threadLocal.get();
    }

    private static String suffix = null;

    public static void initSuffix(String suffix) {
        JPressHandler.suffix = suffix;
    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (StrUtils.isBlank(suffix) && target.indexOf('.') != -1) {
            return;
        }

        //启用伪静态
        if (StrUtils.isNotBlank(suffix) && target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
        }


        try {
            threadLocal.set(target);
            request.setAttribute("CPATH", request.getContextPath());
            next.handle(target, request, response, isHandled);
        } finally {
            threadLocal.remove();
        }
    }


}
