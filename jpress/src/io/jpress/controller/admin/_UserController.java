package io.jpress.controller.admin;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.User;

@UrlMapping(url="/admin/user",viewPath ="/WEB-INF/admin/user")
public class _UserController extends BaseAdminController<User> {
	
	public void index(){
		keepPara();
		super.index();
	}
	
	public void info(){
		keepPara();
	}
	
}
