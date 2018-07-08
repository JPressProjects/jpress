package io.jpress.core.web.base;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before(AdminInterceptor.class)
public abstract class AdminControllerBase extends JbootController {

    @Override
    public void render(String view) {
        if (view.startsWith("/")) {
            super.render(view);
        } else {
            super.render("/WEB-INF/views/admin/" + view);
        }
    }

    public void render(Ret ret) {
        renderJson(ret);
    }
}
