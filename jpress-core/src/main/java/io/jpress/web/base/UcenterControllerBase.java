package io.jpress.web.base;

import com.jfinal.aop.Before;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({UserInterceptor.class, UserCenterInterceptor.class})
public abstract class UcenterControllerBase extends ControllerBase {


}
