package io.jpress.web.front;

import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressAppConfig;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/")
public class IndexController extends TemplateControllerBase {

    private static JPressAppConfig config = Jboot.config(JPressAppConfig.class);

    public void index() {
        forwardAction(config.getIndexAction());
    }
}
