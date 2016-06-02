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
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Comment;
import io.jpress.model.User;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin/comment", viewPath = "/WEB-INF/admin/comment")
@Before(ActionCacheClearInterceptor.class)
public class _CommentController extends JBaseCRUDController<Comment> {

	private String getContentModule() {
		return getPara("m");
	}

	private String getType() {
		return getPara("t");
	}
	
	@Override
	public void index() {
		super.index();
		setAttr("module", Jpress.currentTemplate().getModuleByName(getContentModule()));
		setAttr("delete_count", mDao.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_DELETE));
		setAttr("draft_count", mDao.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_DRAFT));
		setAttr("normal_count", mDao.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_NORMAL));
		setAttr("count", mDao.findCountInNormalByModule(getContentModule()));
	}

	@Override
	public Page<Comment> onIndexDataLoad(int pageNumber, int pageSize) {
		return mDao.doPaginateWithContent(pageNumber, pageSize, getContentModule(), getType(), null , getPara("s"));
	}

	@Override
	public void save() {
		Comment comment = getModel(Comment.class);
		String username = getPara("username");
		if (StringUtils.isNotBlank(username)) {
			User user = User.DAO.findUserByUsername(username);
			if (user == null) {
				renderAjaxResultForError("系统没有该用户：" + username);
				return;
			}
			comment.setUserId(user.getId());
		}

		comment.saveOrUpdate();

		renderAjaxResultForSuccess("ok");
	}
}
