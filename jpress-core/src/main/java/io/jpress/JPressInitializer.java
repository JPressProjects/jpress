package io.jpress;

import com.jfinal.config.Constants;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.server.listener.JbootAppListenerBase;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.module.ModuleManager;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.web.captcha.JPressCaptchaCache;
import io.jpress.web.handler.FakeStaticHandler;
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
        handlers.add(0, new FakeStaticHandler());
    }


    @Override
    public void onJFinalStarted() {

        ModuleManager.me().init();
        MenuManager.me().init();
        WechatAddonManager.me().init();


    }

}
