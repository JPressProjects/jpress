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
package io.jpress.controller.front;

import io.jpress.Consts;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.EncryptCookieUtils;
import io.jpress.utils.HashUtils;
import io.jpress.utils.StringUtils;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;

@UrlMapping(url = Consts.USER_BASE_URL)
@Before(UserInterceptor.class)
public class UserController extends BaseFrontController {

	@Clear
	public void index() {
		String action = getPara();
		if (StringUtils.isNotBlank(action)) {
			keepPara();
			render(String.format("user_%s.html", action));
		} else {
			renderError(404);
		}
	}

	// 固定登陆的url
	@Clear
	public void login() {
		keepPara();
		render("user_login.html");
	}

	@Clear
	public void doLogin() {
		long errorTimes = EncryptCookieUtils.getLong(this, "_login_errors", 0);
		if (errorTimes >= 3) {
			if (!validateCaptcha("_login_captcha")) { // 验证码没验证成功！
				if (isAjaxRequest()) {
					renderAjaxResultForError("没有该用户");
				} else {
					redirect(Consts.LOGIN_BASE_URL);
				}
				return;
			}
		}

		String username = getPara("username");
		String password = getPara("password");
		String from = getPara("from");

		User user = User.findUserByUsername(username);
		if (null == user) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				redirect(Consts.LOGIN_BASE_URL);
			}
			EncryptCookieUtils.put(this, "_login_errors", errorTimes + 1);
			return;
		}

		if (HashUtils.verlifyUser(user, password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			EncryptCookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登陆成功");
			} else {
				if (StringUtils.isNotEmpty(from)) {
					redirect(from);
				} else {
					redirect(Consts.USER_CENTER_BASE_URL);
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				redirect(Consts.LOGIN_BASE_URL);
			}
			EncryptCookieUtils.put(this, "_login_errors", errorTimes + 1);
		}
	}

	@Before(UCodeInterceptor.class)
	public void logout() {
		EncryptCookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		redirect("/");
	}

	@Clear
	public void doRegister() {
		if (!validateCaptcha("_register_captcha")) { // 验证码没验证成功！
			renderAjaxResultForError("not validate captcha");
			return;
		}

		User user = getModel(User.class);
		if (null == user) {
			renderAjaxResultForError("not get user");
			return;
		}

		if (user.getCreateSource() == null) {
			user.setCreateSource("register");
		}
		user.setCreated(new Date());
		user.save();

		renderAjaxResultForSuccess();
		MessageKit.sendMessage(Actions.USER_CREATED, user);
	}

	public void center() {
		keepPara();
		String action = getPara(0, "index");
		render(String.format("ucenter_%s.html", action));
	}
}
