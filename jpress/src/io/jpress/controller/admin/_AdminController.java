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
package io.jpress.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.ehcache.CacheKit;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.EncryptCookieUtils;
import io.jpress.utils.HashUtils;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin", viewPath = "/WEB-INF/admin")
@Before(AdminInterceptor.class)
public class _AdminController extends JBaseController {

	public void index() {
		render("index.html");
	}

	@Clear
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (!StringUtils.areNotEmpty(username, password)) {
			render("login.html");
			return;
		}

		User user = User.findUserByUsername(username);

		if (null == user) {
			renderAjaxResultForError("没有该用户");
			return;
		}

		if (HashUtils.verlifyUser(user, password) && user.isAdministrator()) {

			MessageKit.sendMessage(Actions.USER_LOGINED, user);

			EncryptCookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			CacheKit.put("user", user.getId(), user);
			renderAjaxResultForSuccess("登陆成功");
		} else {
			renderAjaxResultForError("密码错误");
		}
	}

	public void logout() {
		EncryptCookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		redirect("/admin");
	}

}
