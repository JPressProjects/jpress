package io.jpress.core;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class JTokenInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		if(inv.getController().validateToken()){
			inv.invoke();
		}else{
			Controller c = inv.getController();
			if(c instanceof JBaseController){
				((JBaseController)c).renderAjaxResultForError("非法提交");
			}else{
				c.renderError(404);
			}
		}
	}

}
