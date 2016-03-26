package io.jpress.controller.front;

import io.jpress.Consts;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.EncryptCookieUtils;
import io.jpress.utils.HashUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;

@UrlMapping(url = "/u")
@Before(UserInterceptor.class)
public class UserController extends BaseFrontController {

	// http://www.xxx.com/u/123 user.id = 123
	public void index() {
		long id = getParaToLong(0, (long) 0);
		User user = User.DAO.findById(id);
		setAttr("user", user);
		render("user.html");
	}

	@Clear
	public void login() {
		render("user_login.html");
	}
	
	@Clear
	public void doLogin() {
		String username = getPara("username");
		String password = getPara("password");

		User user = User.findUserByUsername(username);
		if (null == user) {
			renderAjaxResultForError("没有该用户");
			return;
		}

		if (HashUtils.verlify(user, password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			EncryptCookieUtils.put(this, Consts.COOKIE_LOGIN_USER_ID,user.getId());
			renderAjaxResultForSuccess("登陆成功");
		}else{
			renderAjaxResultForError("密码错误");
		}
	}
	
	@Clear
	public void register() {
		render("user_register.html");
	}
	
	@Clear
	public void doRegister() {
		// 插入数据库
		// 发送短信
		// 查看推荐人，并给推荐人发送或奖品的消息
		// 查看推荐人上级，给上级现金

		// xxxxx
		// xxxxx

		// 插入数据库
		// 发送用注册消息

		User user = new User();
		
		user.setCreateSource("register");
		
		

		user.save();
		MessageKit.sendMessage(Actions.USER_CREATED, user);
	}
	
	public void center(){
		renderText("logined ok!");
//		render("user_center.html");
	}
}
