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
package io.jpress.admin.controller;

import java.math.BigInteger;
import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.CommentQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.utils.JsoupUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/comment", viewPath = "/WEB-INF/admin/comment")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CommentController extends JBaseCRUDController<Comment> {

	private String getModule() {
		return getPara("m");
	}

	private String getType() {
		return getPara("t");
	}

	@Override
	public void index() {

		keepPara();

		setAttr("module", TemplateManager.me().currentTemplateModule(getModule()));
		setAttr("delete_count", CommentQuery.me().findCountByModuleAndStatus(getModule(), Comment.STATUS_DELETE));
		setAttr("draft_count", CommentQuery.me().findCountByModuleAndStatus(getModule(), Comment.STATUS_DRAFT));
		setAttr("normal_count", CommentQuery.me().findCountByModuleAndStatus(getModule(), Comment.STATUS_NORMAL));
		setAttr("count", CommentQuery.me().findCountInNormalByModule(getModule()));

		super.index();
	}

	@Override
	public Page<Comment> onIndexDataLoad(int pageNumber, int pageSize) {

		BigInteger contentId = getParaToBigInteger("cid");
		BigInteger parentCommentId = getParaToBigInteger("pid");

		if (StringUtils.isNotBlank(getPara("s"))) {
			return CommentQuery.me().paginateWithContent(pageNumber, pageSize, getModule(), getType(), contentId,
					parentCommentId, getPara("s"));
		}
		return CommentQuery.me().paginateWithContentNotInDelete(pageNumber, pageSize, getModule(), getType(), contentId,
				parentCommentId);
	}

	@Override
	public void edit() {
		BigInteger id = getParaToBigInteger("id");
		Comment comment = CommentQuery.me().findById(id);
		setAttr("comment", comment);
	}

	@Before(UCodeInterceptor.class)
	public void trash() {
		Comment c = CommentQuery.me().findById(getParaToBigInteger("id"));
		if (c != null) {
			c.setStatus(Comment.STATUS_DELETE);
			if (c.saveOrUpdate()) {
				renderAjaxResultForSuccess();
			} else {
				renderAjaxResultForError("restore error!");
			}
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void restore() {
		BigInteger id = getParaToBigInteger("id");
		Comment c = CommentQuery.me().findById(id);
		if (c != null && c.isDelete()) {
			c.setStatus(Content.STATUS_DRAFT);
			if (c.saveOrUpdate()) {
				renderAjaxResultForSuccess("success");
			} else {
				renderAjaxResultForError("restore error!");
			}
		} else {
			renderAjaxResultForError("restore error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void pub() {
		BigInteger id = getParaToBigInteger("id");
		Comment c = CommentQuery.me().findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_NORMAL);
			if (c.saveOrUpdate()) {
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
		Comment c = CommentQuery.me().findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_DRAFT);
			if (c.saveOrUpdate()) {
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
		final Comment c = CommentQuery.me().findById(id);
		if (c != null) {
			if (c.delete()) {
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
			User user = UserQuery.me().findUserByUsername(username);
			if (user == null) {
				renderAjaxResultForError("系统没有该用户：" + username);
				return;
			}
			comment.setUserId(user.getId());
		}
		if (comment.saveOrUpdate()) {
			comment.updateCommentCount();
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	public void reply_layer() {
		BigInteger id = getParaToBigInteger("id");
		setAttr("comment", CommentQuery.me().findById(id));
	}

	public void reply() {
		Comment comment = getModel(Comment.class);

		comment.setType(Comment.TYPE_COMMENT);
		comment.setIp(getIPAddress());
		comment.setAgent(getUserAgent());
		User user = getLoginedUser();
		String author = StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getUsername();
		comment.setAuthor(author);
		comment.setEmail(user.getEmail());
		comment.setStatus(Comment.STATUS_NORMAL);
		comment.setUserId(user.getId());
		comment.setCreated(new Date());

		comment.setText(JsoupUtils.getBodyHtml(comment.getText()));

		comment.save();
		renderAjaxResultForSuccess();
	}
}
