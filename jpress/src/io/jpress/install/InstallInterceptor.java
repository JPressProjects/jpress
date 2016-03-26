package io.jpress.install;

import io.jpress.core.Jpress;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @title 用于拦截web如果已经安装，则不让其访问InstallController
 * @author Michael Yang （http://fuhai.me）
 * @version 1.0
 * @created 2016年2月1日
 */
public class InstallInterceptor implements Interceptor {
	
	@Override
	public void intercept(Invocation inv) {
		if (Jpress.isInstalled()) {
			inv.getController().redirect("/");
		} else {
			inv.invoke();
		}
	}

}
