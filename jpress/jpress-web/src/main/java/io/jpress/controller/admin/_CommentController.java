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

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.Jpress;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.listener.Actions;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.CommentQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.plugin.message.MessageKit;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/comment", viewPath = "/WEB-INF/admin/comment")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
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
		setAttr("delete_count", CommentQuery.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_DELETE));
		setAttr("draft_count", CommentQuery.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_DRAFT));
		setAttr("normal_count", CommentQuery.findCountByModuleAndStatus(getContentModule(), Comment.STATUS_NORMAL));
		setAttr("count", CommentQuery.findCountInNormalByModule(getContentModule()));
	}

	@Override
	public Page<Comment> onIndexDataLoad(int pageNumber, int pageSize) {
		if (StringUtils.isNotBlank(getPara("s"))) {
			return CommentQuery.paginateWithContent(pageNumber, pageSize, getContentModule(), getType(), null, getPara("s"));
		}
		return CommentQuery.paginateWithContentNotInDelete(pageNumber, pageSize, getContentModule());
	}

	@Before(UCodeInterceptor.class)
	public void trash() {
		Comment c = CommentQuery.findById(getParaToBigInteger("id"));
		if (c != null) {
			c.setStatus(Comment.STATUS_DELETE);
			c.saveOrUpdate();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void restore() {
		BigInteger id = getParaToBigInteger("id");
		Comment c = CommentQuery.findById(id);
		if (c != null && c.isDelete()) {
			c.setStatus(Content.STATUS_DRAFT);
			c.saveOrUpdate();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("restore error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void pub() {
		BigInteger id = getParaToBigInteger("id");
		Comment c = CommentQuery.findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_NORMAL);
			if (c.saveOrUpdate()) {
				MessageKit.sendMessage(Actions.COMMENT_UPDATE, c);
				renderAjaxResultForSuccess("success");
			} else {
				renderAjaxResultForError("pub fail!");
			}
		} else {
			renderAjaxResultForError("pub error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void draft() {
		BigInteger id = getParaToBigInteger("id");
		Comment c = CommentQuery.findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_DRAFT);
			if (c.saveOrUpdate()) {
				MessageKit.sendMessage(Actions.COMMENT_UPDATE, c);
				renderAjaxResultForSuccess("success");
			} else {
				renderAjaxResultForError("draft fail!");
			}
		} else {
			renderAjaxResultForError("draft error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		final Comment c = CommentQuery.findById(id);
		if (c != null) {
			if (c.delete()) {
				MessageKit.sendMessage(Actions.COMMENT_DELETE, c);
				renderAjaxResultForSuccess();
				return;
			}
		}
		renderAjaxResultForError();
	}

	@Override
	public void save() {
		Comment comment = getModel(Comment.class);
		String username = getPara("username");
		if (StringUtils.isNotBlank(username)) {
			User user = UserQuery.findUserByUsername(username);
			if (user == null) {
				renderAjaxResultForError("系统没有该用户：" + username);
				return;
			}
			comment.setUserId(user.getId());
		}
		if (comment.saveOrUpdate()) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}

	}
}
