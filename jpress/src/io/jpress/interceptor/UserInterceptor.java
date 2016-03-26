package io.jpress.interceptor;

import io.jpress.model.User;
import io.jpress.utils.HashUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class UserInterceptor implements Interceptor {


	@Override
	public void intercept(Invocation inv) {
		
		User  user = InterUtils.tryToGetUser(inv);
		
		if(user != null ){
			inv.getController().setAttr("user", user);
			inv.getController().setAttr("ucode", HashUtils.generateUcode(user));
			inv.invoke();
		}else{
			inv.getController().redirect("/u/login");
		}

	}

}
