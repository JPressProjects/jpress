package io.jpress.controller.admin;

import com.jfinal.aop.Before;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;

@UrlMapping(url="/admin/plugin" ,viewPath ="/WEB-INF/admin/plugin")
@Before(AdminInterceptor.class)
public class _PluginController extends JBaseController {

	public void index(){
		keepPara();
	}
	
	public void install(){
		keepPara();
	}
	
	public void edit(){
		keepPara();
	}
	
}
