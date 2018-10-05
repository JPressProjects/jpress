package io.jpress.web.base;

import com.jfinal.aop.Before;
import io.jpress.web.interceptor.UserInterceptor;
import io.jpress.web.interceptor.UserMustLoginedInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({UserInterceptor.class, UserMustLoginedInterceptor.class})
public abstract class UserControllerBase extends ControllerBase {


}