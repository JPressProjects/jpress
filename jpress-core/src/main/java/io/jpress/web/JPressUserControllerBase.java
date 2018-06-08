package io.jpress.web;

import com.jfinal.aop.Before;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before(JPressUserInterceptor.class)
public abstract class JPressUserControllerBase extends JPressFrontControllerBase {
}
