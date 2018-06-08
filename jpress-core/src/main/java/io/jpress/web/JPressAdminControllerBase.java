package io.jpress.web;

import com.jfinal.aop.Before;
import io.jboot.web.controller.JbootController;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before(JPressAdminInterceptor.class)
public abstract class JPressAdminControllerBase extends JbootController {
}
