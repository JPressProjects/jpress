package io.jpress.web.handler;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 安全控制 handler
 * @Description: 不让访问直接模板，防止下载。不让让访问jsp，防止jsp木马
 * @Package io.jpress.web.admin
 */
public class SafeControlHandler extends Handler {


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.startsWith("/templates") && target.endsWith(".html")) {
            HandlerKit.renderError404(request, response, isHandled);
            return;
        }

        if (target.toLowerCase().endsWith(".jsp")) {
            HandlerKit.renderError404(request, response, isHandled);
            return;
        }


        next.handle(target, request, response, isHandled);
    }
}
