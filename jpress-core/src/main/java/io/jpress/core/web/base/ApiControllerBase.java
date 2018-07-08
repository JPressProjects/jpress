package io.jpress.core.web.base;

import com.jfinal.aop.Before;
import io.jboot.web.controller.JbootController;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before(ApiInterceptor.class)
public abstract class ApiControllerBase extends JbootController {
}
