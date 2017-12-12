package io.jpress;

import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.server.listener.JbootAppListenerBase;
import io.jpress.model.Content;
import io.jpress.web.ext.ContentExt;
import io.jpress.web.handler.FakeStaticHandler;
import io.jpress.web.handler.SafeControlHandler;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress
 */
public class JPressAppConfig extends JbootAppListenerBase {

    @Override
    public void onJfinalEngineConfig(Engine engine) {

        engine.addExtensionMethod(Content.class, ContentExt.class);

    }


    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {

        handlers.add(new FakeStaticHandler());
        handlers.add(new SafeControlHandler());
    }
}
