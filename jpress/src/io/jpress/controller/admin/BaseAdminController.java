package io.jpress.controller.admin;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.JModel;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.model.User;

import com.jfinal.aop.Before;


@Before(AdminInterceptor.class)
public class BaseAdminController<M extends JModel<? extends JModel<?>>> extends JBaseCRUDController<M> {

	
	public User getLoginedUser(){
		return getAttr("user");
	}
	
	
	@Override
	public void delete() {
		if(!validateToken()){
			renderAjaxResultForError("非法提交");
		}
		super.delete();
	}
}
