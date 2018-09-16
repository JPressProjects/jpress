package io.jpress.web.handler;


import com.jfinal.handler.Handler;
import io.jboot.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 伪静态处理器
 * @Package io.jpress.web.handler
 */

public class FakeStaticHandler extends Handler {

    private static String suffix = null;

    public static void initSuffix(String suffix) {
        FakeStaticHandler.suffix = suffix;
    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (StringUtils.isBlank(suffix)) {
            next.handle(target, request, response, isHandled);
            return;
        }
        if (target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
        }
        next.handle(target, request, response, isHandled);
    }


}
