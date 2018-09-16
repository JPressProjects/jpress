package io.jpress.web;

import com.jfinal.template.Engine;
import io.jboot.server.listener.JbootAppListenerBase;
import io.jpress.web.sharekit.MainKits;
import io.jpress.web.sharekit.PermissionKits;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class WebInitializer extends JbootAppListenerBase {


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

        OptionInitializer.me().init();
    }

}
