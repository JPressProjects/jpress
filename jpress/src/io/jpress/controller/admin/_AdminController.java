package io.jpress.controller.admin;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.EncryptCookieUtils;
import io.jpress.utils.HashUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

@UrlMapping(url="/admin" ,viewPath ="/WEB-INF/admin")
@Before(AdminInterceptor.class)
public class _AdminController extends JBaseController {
	
	public void index(){
		render("index.html");
	}
	
	
	@Clear
	public void login(){
		String username = getPara("username");
		String password = getPara("password");
		
		
		if(StrKit.isBlank(username) || StrKit.isBlank(password)){
			render("login.html");
			return;
		}
		
		User user = User.findUserByUsername(username);
		
		if(null == user){
			renderAjaxResultForError("没有该用户");
			return;
		}
		
		if(HashUtils.verlify(user, password) && user.isAdministrator()){
			
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			
			EncryptCookieUtils.put(this, Consts.COOKIE_LOGIN_USER_ID, user.getId());
			CacheKit.put("user", user.getId(), user);
			renderAjaxResultForSuccess("登陆成功");
		}else{
			renderAjaxResultForError("密码错误");
		}
	}
	
	public void logout(){
		EncryptCookieUtils.remove(this, Consts.COOKIE_LOGIN_USER_ID);
		redirect("/admin");
	}
	
	
	public void blank(){
		render("blank.html");
	}
	

}
