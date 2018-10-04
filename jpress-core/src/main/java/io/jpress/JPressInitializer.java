package io.jpress;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.server.listener.JbootAppListenerBase;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.web.base.UTMInterceptor;
import io.jpress.web.captcha.JPressCaptchaCache;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.render.JPressRenderFactory;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class JPressInitializer extends JbootAppListenerBase {

    @Override
    public void onJfinalConstantConfig(Constants constants) {
        constants.setRenderFactory(new JPressRenderFactory());
        constants.setCaptchaCache(new JPressCaptchaCache());
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        handlers.add(0, new JPressHandler());
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        interceptors.add(new UTMInterceptor());
    }

    @Override
    public void onJFinalStarted() {

        MenuManager.me().init();
        WechatAddonManager.me().init();


    }

}
