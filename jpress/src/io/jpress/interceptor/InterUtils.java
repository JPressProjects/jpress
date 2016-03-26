package io.jpress.interceptor;

import io.jpress.Consts;
import io.jpress.model.User;
import io.jpress.utils.EncryptCookieUtils;

import com.jfinal.aop.Invocation;

public class InterUtils {

	public static User tryToGetUser(Invocation inv) {
		User user = inv.getController().getAttr("user");
		if (user == null) {
			String userId = EncryptCookieUtils.get(inv.getController(),
					Consts.COOKIE_LOGIN_USER_ID);

			if (userId != null && !"".equals(userId))
				user = User.findUserById(Long.parseLong(userId));
		}
		return user;
	}

}
