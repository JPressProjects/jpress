package io.jpress.module.article;

import com.jfinal.template.Engine;
import io.jboot.server.listener.JbootAppListenerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class AppListener extends JbootAppListenerBase {


    @Override
    public void onJfinalEngineConfig(Engine engine) {
        engine.addSharedStaticMethod(ArticleModuleKit.class);
    }

}
