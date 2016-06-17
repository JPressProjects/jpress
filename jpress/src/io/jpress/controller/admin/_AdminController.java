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

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.Jpress;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.Module;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin", viewPath = "/WEB-INF/admin")
@RouterNotAllowConvert
public class _AdminController extends JBaseController {

	@Before(ActionCacheClearInterceptor.class)
	public void index() {
		setAttr("modules", Jpress.currentTemplate().getModules());
		List<Module> moduleList = Jpress.currentTemplate().getModules();

		if (moduleList != null && moduleList.size() > 0) {
			String moduels[] = new String[moduleList.size()];
			for (int i = 0; i < moduleList.size(); i++) {
				moduels[i] = moduleList.get(i).getName();
			}

			List<Content> contents = Content.DAO.findListInNormal(1, 20, null, null, null, null, moduels, null, null,
					null, null, null, null, null);
			setAttr("contents", contents);
		}

		Page<Comment> commentPage = Comment.DAO.doPaginateWithContentNotInDelete(1, 10, null);
		if (commentPage != null) {
			setAttr("comments", commentPage.getList());
		}

		render("index.html");
	}

	@Clear(AdminInterceptor.class)
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (!StringUtils.areNotEmpty(username, password)) {
			render("login.html");
			return;
		}

		User user = User.DAO.findUserByUsername(username);

		if (null == user) {
			renderAjaxResultForError("没有该用户");
			return;
		}

		if (EncryptUtils.verlifyUser(user, password) && user.isAdministrator()) {

			MessageKit.sendMessage(Actions.USER_LOGINED, user);

			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());

			renderAjaxResultForSuccess("登陆成功");
		} else {
			renderAjaxResultForError("密码错误");
		}
	}

	@Before(UCodeInterceptor.class)
	public void logout() {
		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		redirect("/admin");
	}

}
