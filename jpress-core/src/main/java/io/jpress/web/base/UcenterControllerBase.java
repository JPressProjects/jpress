package io.jpress.web.base;

import com.jfinal.aop.Before;
import io.jpress.web.interceptor.UserCenterInterceptor;
import io.jpress.web.interceptor.UserInterceptor;
import io.jpress.web.interceptor.UserMustLoginedInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({UserInterceptor.class,
        UserMustLoginedInterceptor.class,
        UserCenterInterceptor.class})
public abstract class UcenterControllerBase extends ControllerBase {


    @Override
    public void render(String view) {
        if (view.startsWith("/")) {
            super.render(view);
        } else {
            super.render("/WEB-INF/views/ucenter/" + view);
        }
    }


    public int getPagePara() {
        return getParaToInt("page", 1);
    }


}
