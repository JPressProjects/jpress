package io.jpress.web;

import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.server.listener.JbootAppListenerBase;
import io.jpress.JPressApplication;
import io.jpress.web.handler.FakeStaticHandler;
import io.jpress.web.sharekit.MainKits;
import io.jpress.web.sharekit.PermissionKits;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class JPressInitializer extends JbootAppListenerBase {



    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        handlers.add(0, new FakeStaticHandler());
    }

    @Override
    public void onJfinalEngineConfig(Engine engine) {

        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layout.html");
        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layer.html");
        engine.addSharedFunction("/WEB-INF/views/admin/_layout/_paginate.html");
        engine.addSharedFunction("/WEB-INF/views/admin/user/_user_detail_layer.html");

        engine.addSharedStaticMethod(MainKits.class);
        engine.addSharedStaticMethod(PermissionKits.class);

    }

    @Override
    public void onJFinalStarted() {

        JPressApplication.me().init();
        OptionInitializer.me().init();

    }

}
