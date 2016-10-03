/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.front.controller;

import java.math.BigInteger;
import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;

import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.ui.freemarker.tag.UserContentPageTag;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_USER)
@Before(UserInterceptor.class)
public class UserController extends BaseFrontController {

	@Clear(UserInterceptor.class)
	public void index() {
		String action = getPara();
		if (StringUtils.isBlank(action)) {
			renderError(404);
		}

		keepPara();

		BigInteger userId = StringUtils.toBigInteger(action, null);

		if (userId != null) {
			User user = UserQuery.me().findById(userId);
			if (user != null) {
				setAttr("user", user);
				render(String.format("user_detail.html", action));
			} else {
				renderError(404);
			}
		} else {
			if ("detail".equalsIgnoreCase(action)) {
				renderError(404);
			}
			render(String.format("user_%s.html", action));
		}
	}

	@Clear(UserInterceptor.class)
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登录的url
	public void login() {
		keepPara();

		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("user_login.html");
			return;
		}

		long errorTimes = CookieUtils.getLong(this, "_login_errors", 0);
		if (errorTimes >= 3) {
			if (!validateCaptcha("_login_captcha")) { // 验证码没验证成功！
				if (isAjaxRequest()) {
					renderAjaxResultForError("没有该用户");
				} else {
					redirect(Consts.ROUTER_USER_LOGIN);
				}
				return;
			}
		}

		User user = UserQuery.me().findUserByUsername(username);
		if (null == user) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
			return;
		}

		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登录成功");
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				setAttr("errorMsg", "密码错误");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
		}
	}

	@Before(UCodeInterceptor.class)
	public void logout() {
		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		redirect("/");
	}

	@Clear(UserInterceptor.class)
	public void doRegister() {

		if (!validateCaptcha("_register_captcha")) { // 验证码没验证成功！
			renderForRegister("not validate captcha", Consts.ERROR_CODE_NOT_VALIDATE_CAPTHCHE);
			return;
		}

		keepPara();

		String username = getPara("username");
		String email = getPara("email");
		String mobile = getPara("mobile");
		String password = getPara("password");
		String confirm_password = getPara("confirm_password");

		if (StringUtils.isBlank(username)) {
			renderForRegister("username is empty!", Consts.ERROR_CODE_USERNAME_EMPTY);
			return;
		}

		if (!StringUtils.isNotBlank(email)) {
			renderForRegister("email is empty!", Consts.ERROR_CODE_EMAIL_EMPTY);
			return;
		} else {
			email = email.toLowerCase();
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForRegister("password is empty!", Consts.ERROR_CODE_PASSWORD_EMPTY);
			return;
		}

		if (StringUtils.isNotEmpty(confirm_password)) {
			if (!confirm_password.equals(password)) {
				renderForRegister("password is not equals confirm_password!", Consts.ERROR_CODE_PASSWORD_EMPTY);
				return;
			}
		}

		if (UserQuery.me().findUserByUsername(username) != null) {
			renderForRegister("username has exist!", Consts.ERROR_CODE_USERNAME_EXIST);
			return;
		}

		if (UserQuery.me().findUserByEmail(email) != null) {
			renderForRegister("email has exist!", Consts.ERROR_CODE_EMAIL_EXIST);
			return;
		}

		if (null != mobile && UserQuery.me().findUserByMobile(mobile) != null) {
			renderForRegister("phone has exist!", Consts.ERROR_CODE_PHONE_EXIST);
			return;
		}

		User user = new User();
		user.setUsername(username);
		user.setNickname(username);
		user.setEmail(email);
		user.setMobile(mobile);

		String salt = EncryptUtils.salt();
		password = EncryptUtils.encryptPassword(password, salt);
		user.setPassword(password);
		user.setSalt(salt);
		user.setCreateSource("register");
		user.setCreated(new Date());

		if (user.save()) {
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());

			if (isAjaxRequest()) {
				renderAjaxResultForSuccess();
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			renderAjaxResultForError();
		}
	}

	private void renderForRegister(String message, int errorCode) {
		String referer = getRequest().getHeader("Referer");
		if (isAjaxRequest()) {
			renderAjaxResult(message, errorCode);
		} else {
			redirect(referer + "?errorcode=" + errorCode);
		}
	}

	public void center() {
		keepPara();
		String action = getPara(0, "index");
		render(String.format("user_center_%s.html", action));

		int pageNumber = getParaToInt(1, 1);
		BigInteger userId = getLoginedUser().getId();

		setAttr(UserContentPageTag.TAG_NAME, new UserContentPageTag(action, userId, pageNumber));
		setAttr("action", action);
		setAttr(action, "active");
	}

}
