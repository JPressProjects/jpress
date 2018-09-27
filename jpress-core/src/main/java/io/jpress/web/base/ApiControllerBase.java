package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({ApiInterceptor.class, UserInterceptor.class})
public abstract class ApiControllerBase extends ControllerBase {

    public void renderFailJson() {
        renderJson(Ret.fail());
    }


    public void renderFailJson(String message) {
        renderJson(Ret.fail("message", message));
    }
}
