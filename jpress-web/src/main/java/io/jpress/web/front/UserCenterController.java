package io.jpress.web.front;

import com.jfinal.aop.Before;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.ControllerBase;
import io.jpress.web.base.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/ucenter")
@Before({UserInterceptor.class, UserCenterInterceptor.class})
public class UserCenterController extends ControllerBase {


    @Override
    public void render(String view) {
        super.render("/WEB-INF/views/ucenter/" + view);
    }



    /**
     * 用户中心首页
     */
    public void index() {
        render("index.html");
    }


}
