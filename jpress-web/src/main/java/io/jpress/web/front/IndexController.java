package io.jpress.web.front;

import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConfig;
import io.jpress.core.web.base.FrontControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/")
public class IndexController extends FrontControllerBase {

    private static JPressConfig config = Jboot.config(JPressConfig.class);

    public void index() {
        forwardAction(config.getIndexAction());
    }
}
