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

import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.core.cache.ActionCacheManager;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/comment")
public class CommentController extends BaseFrontController {

	public void index() {
		renderError(404);
	}

	public void submit() {

		String gotoUrl = getPara("goto");
		if (gotoUrl == null) {
			gotoUrl = getRequest().getHeader("Referer");
		}

		String anchor = getPara("anchor");
		if (gotoUrl != null && anchor != null) {
			gotoUrl += "#" + anchor;
		}

		// 是否开启验证码功能
		Boolean comment_need_captcha = OptionQuery.me().findValueAsBool("comment_need_captcha");
		if (comment_need_captcha != null && comment_need_captcha == true) {
			if (!validateCaptcha("comment_captcha")) { // 验证码验证失败
				renderForCommentError("validate captcha fail", 1);
				return;
			}
		}

		BigInteger userId = StringUtils.toBigInteger(CookieUtils.get(this, Consts.COOKIE_LOGINED_USER), null);

		// 允许未登录用户评论
		Boolean comment_allow_not_login = OptionQuery.me().findValueAsBool("comment_allow_not_login");

		// 允许未登录用户评论
		if (comment_allow_not_login == null || comment_allow_not_login == false) {
			if (userId == null) {
				String redirect = Consts.ROUTER_USER_LOGIN;
				if (StringUtils.isNotBlank(gotoUrl)) {
					redirect += "?goto=" + StringUtils.urlEncode(gotoUrl);
				}
				redirect(redirect);
				return;
			}
		}

		String status = Comment.STATUS_NORMAL;
		Boolean comment_must_audited = OptionQuery.me().findValueAsBool("comment_must_audited");
		if (comment_must_audited != null && comment_must_audited) {
			status = Comment.STATUS_DRAFT;
		}

		BigInteger contentId = getParaToBigInteger("cid");
		if (contentId == null) {
			renderForCommentError("comment fail,content id is null.", 1);
			return;
		}

		Content content = ContentQuery.me().findById(contentId);
		if (content == null) {
			renderForCommentError("find not find the content, maybe it has bean deleted.", 1);
			return;
		}

		if (!content.isCommentEnable()) {
			renderForCommentError("the comment function of the content has been closed.", 1);
			return;
		}

		String text = getPara("text");
		if (StringUtils.isBlank(text)) {
			renderForCommentError("comment fail,text is blank.", 2);
			return;
		}

		String author = getPara("author");
		String email = getPara("email");

		String ip = getIPAddress();
		String agent = getUserAgent();
		String type = Comment.TYPE_COMMENT;

		if (userId != null) {
			User user = UserQuery.me().findById(userId);
			if (user != null) {
				author = StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getUsername();
			}
		}

		if (StringUtils.isBlank(author)) {
			String defautAuthor = OptionQuery.me().findValue("comment_default_nickname");
			author = StringUtils.isNotBlank(defautAuthor) ? defautAuthor : "网友";
		}

		BigInteger parentId = getParaToBigInteger("parent_id");

		Comment comment = new Comment();
		comment.setContentModule(content.getModule());
		comment.setType(Comment.TYPE_COMMENT);
		comment.setContentId(content.getId());
		comment.setText(text);
		comment.setIp(ip);
		comment.setAgent(agent);
		comment.setAuthor(author);
		comment.setEmail(email);
		comment.setType(type);
		comment.setStatus(status);
		comment.setUserId(userId);
		comment.setCreated(new Date());
		comment.setParentId(parentId);

		if (comment.save()) {
			ActionCacheManager.clearCache();
		}

		if (isAjaxRequest()) {
			renderAjaxResultForSuccess();
			return;
		}

		if (gotoUrl != null) {
			redirect(gotoUrl);
			return;
		}

		renderText("comment ok");

	}

	private void renderForCommentError(String message, int errorCode) {
		String referer = getRequest().getHeader("Referer");
		if (isAjaxRequest()) {
			renderAjaxResult(message, errorCode);
		} else {
			redirect(referer + "#" + getPara("anchor"));
		}
	}

}
