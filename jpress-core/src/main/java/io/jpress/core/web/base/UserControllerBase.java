package io.jpress.core.web.base;

import com.jfinal.aop.Before;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before(UserInterceptor.class)
public abstract class UserControllerBase extends FrontControllerBase {
}
