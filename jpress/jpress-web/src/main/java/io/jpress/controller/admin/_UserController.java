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

import java.math.BigInteger;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import io.jpress.Consts;
import io.jpress.core.JBaseCRUDController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/user", viewPath = "/WEB-INF/admin/user")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _UserController extends JBaseCRUDController<User> {

	@Override
	public Page<User> onIndexDataLoad(int pageNumber, int pageSize) {
		setAttr("userCount", UserQuery.me().findCount());
		setAttr("adminCount", UserQuery.me().findAdminCount());

		return UserQuery.me().paginate(pageNumber, pageSize);
	}

	@Override
	public boolean onModelSaveBefore(User m) {

		String password = getPara("password");

		// 修改了密码
		if (m.getId() != null && StringUtils.isNotEmpty(password)) {
			User dbUser = UserQuery.me().findById(m.getId());
			m.setSalt(dbUser.getSalt());
			password = EncryptUtils.encryptPassword(password, dbUser.getSalt());
			m.setPassword(password);
		}

		// 新建用户
		if (m.getId() == null && StringUtils.isNotEmpty(password)) {
			String salt = EncryptUtils.salt();
			m.setSalt(salt);

			password = EncryptUtils.encryptPassword(password, salt);
			m.setPassword(password);
		}

		UploadFile uf = getFile();
		if (uf != null) {
			String newPath = AttachmentUtils.moveFile(uf);
			m.setAvatar(newPath);
		} else {
			String url = getPara("user_avatar");
			if (StringUtils.isNotBlank(url)) {
				m.setAvatar(url.trim());
			}
		}

		return super.onModelSaveBefore(m);
	}

	@Override
	public boolean onModelSaveAfter(User m) {
		User user = getAttr(Consts.ATTR_USER);
		if (user != null && m.getId().compareTo(user.getId()) == 0) {
			removeAttr(Consts.ATTR_USER);
		}
		return super.onModelSaveAfter(m);
	}

	public void info() {
		User user = getAttr(Consts.ATTR_USER);
		if (user != null) {
			user = UserQuery.me().findById(user.getId());
		}
		setAttr("user", user);
		render("edit.html");
	}

	public void frozen() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			User user = UserQuery.me().findById(id);
			user.setStatus(User.STATUS_FROZEN);
			user.update();
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	public void restore() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			User user = UserQuery.me().findById(id);
			user.setStatus(User.STATUS_NORMAL);
			user.update();
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}
	
	
	@Override
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		if(id == null ){
			renderAjaxResultForError();
			return;
		}
		
		User user = getAttr(Consts.ATTR_USER);
		if(user.getId().compareTo(id) == 0){
			renderAjaxResultForError("不能删除自己...");
			return;
		}
		
		super.delete();
	}

}
