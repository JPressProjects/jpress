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

import java.math.BigInteger;
import java.util.Date;

import io.jpress.Consts;
import io.jpress.core.cache.ActionCacheManager;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.router.RouterMapping;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/comment")
public class CommentController extends BaseFrontController {

	public void index() {

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

		BigInteger userId = StringUtils.toBigInteger(CookieUtils.get(this, Consts.COOKIE_LOGINED_USER), null);

		// 允许未登陆用户评论
		Boolean comment_allow_not_login = OptionQuery.findValueAsBool("comment_allow_not_login");

		// 允许未登陆用户评论
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
		Boolean comment_must_audited = OptionQuery.findValueAsBool("comment_must_audited");
		if (comment_must_audited != null && comment_must_audited) {
			status = Comment.STATUS_DRAFT;
		}

		BigInteger contentId = getParaToBigInteger("cid");
		Content content = null;
		if (contentId != null) {
			content = ContentQuery.findById(contentId);
		} else {
			renderForCommentError("comment fail,content id is null!", 1);
			return;
		}

		String text = getPara("text");
		if (!StringUtils.isNotBlank(text)) {
			renderForCommentError("comment fail,text is blank!", 2);
			return;
		}

		String author = getPara("author");
		String email = getPara("email");

		String ip = getIPAddress();
		String agent = getUserAgent();
		String type = Comment.TYPE_COMMENT;

		if (userId != null) {
			User user = UserQuery.findById(userId);
			if (user != null && StringUtils.isNotBlank(user.getNickname())) {
				author = user.getNickname();
			} else {
				author = user.getUsername();
			}
		}

		if (!StringUtils.isNotBlank(author)) {
			String defautAuthor = OptionQuery.findValue("comment_default_nickname");
			author = StringUtils.isNotBlank(defautAuthor) ? defautAuthor : "网友";
		}

		final Comment comment = new Comment();
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

		if (comment.save()) {
			MessageKit.sendMessage(Actions.COMMENT_ADD, comment);
		}

		if (isAjaxRequest()) {
			renderAjaxResultForSuccess();
		} else {
			if (gotoUrl != null) {
				redirect(gotoUrl);
			} else {
				renderText("comment ok");
			}
		}

		ActionCacheManager.clearCache();
	}

	private void renderForCommentError(String message, int errorCode) {
		String referer = getRequest().getHeader("Referer");
		if (isAjaxRequest()) {
			renderAjaxResult(message, errorCode);
		} else {
			redirect(referer + "#" + getPara("anchor"));
		}
	}

	private String getIPAddress() {
		String ip = getRequest().getHeader("X-getRequest()ed-For");
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("X-Forwarded-For");
		}
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("Proxy-Client-IP");
		}
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("WL-Proxy-Client-IP");
		}
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("HTTP_CLIENT_IP");
		}
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getRemoteAddr();
		}
		return ip;
	}

	private String getUserAgent() {
		return getRequest().getHeader("User-Agent");
	}

}
