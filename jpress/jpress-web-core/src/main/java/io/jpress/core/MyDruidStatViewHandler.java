package io.jpress.core;

import java.math.BigInteger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidStatViewHandler;

import io.jpress.Consts;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.StringUtils;

public class MyDruidStatViewHandler extends DruidStatViewHandler {
	
	static String visitPath = "/admin/druid";

	public MyDruidStatViewHandler() {
		super(visitPath);
	}

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.startsWith(visitPath) && Jpress.isInstalled() && Jpress.isLoaded()) {

			String encrypt_key = PropKit.get("encrypt_key");
			String cookieInfo = getCookie(request, Consts.COOKIE_LOGINED_USER);

			String userId = CookieUtils.getFromCookieInfo(encrypt_key, cookieInfo);
			if (StringUtils.isNotBlank(userId)) {
				User user = UserQuery.me().findById(new BigInteger(userId));
				if (user != null && user.isAdministrator()) {
					super.handle(target, request, response, isHandled);
					return;
				}
			}
		}

		next.handle(target, request, response, isHandled);
	}

	private String getCookie(HttpServletRequest request, String name) {
		Cookie cookie = getCookieObject(request, name);
		return cookie != null ? cookie.getValue() : null;
	}

	private Cookie getCookieObject(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}

}
