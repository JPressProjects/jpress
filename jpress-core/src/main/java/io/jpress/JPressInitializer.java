package io.jpress;

import com.jfinal.template.Engine;
import io.jboot.server.listener.JbootAppListenerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class JPressInitializer extends JbootAppListenerBase {

    @Override
    public void onJFinalStarted() {
        JPressAppConfig.me().init();
    }


    @Override
    public void onJfinalEngineConfig(Engine engine) {

        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layout.html");
        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layer.html");
        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_paginate.html");
    }
}
