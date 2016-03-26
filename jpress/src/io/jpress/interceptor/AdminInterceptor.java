package io.jpress.interceptor;

import io.jpress.model.User;
import io.jpress.utils.HashUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class AdminInterceptor implements Interceptor {


	@Override
	public void intercept(Invocation inv) {
		
		User user = InterUtils.tryToGetUser(inv);
		
		if(user != null && user.isAdministrator()){
			inv.getController().setAttr("user", user);
			inv.getController().setAttr("ucode", HashUtils.generateUcode(user));
			inv.invoke();
			return;
		}
		
		inv.getController().redirect("/admin/login");
	}
	

}
