package io.jpress.controller.front;

import java.math.BigInteger;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Comment;
import io.jpress.model.Option;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/comment")
public class CommentController extends JBaseController {

	public void index() {

	}

	public void submit() {
		String gotoUrl = getPara("goto");
		String userId = CookieUtils.get(this, Consts.COOKIE_LOGINED_USER);

		// 必须登录
		Boolean comment_must_logined = Option.findValueAsBool("comment_must_logined");
		if (comment_must_logined != null && comment_must_logined) {
			if (!StringUtils.isNotBlank(userId)) {
				if (isAjaxRequest()) {
					renderAjaxResultForError("user not logined!");
				} else {
					if (gotoUrl != null) {
						redirect(gotoUrl);
					}
				}
				return;
			}
		}

		String status = Comment.STATUS_NORMAL;
		Boolean comment_must_audited = Option.findValueAsBool("comment_must_audited");
		if (comment_must_audited != null && comment_must_audited) {
			status = Comment.STATUS_DRAFT;
		}

		String module = getPara("module");
		String author = getPara("author");
		String text = getPara("text");
		String email = getPara("email");

		String ip = getIPAddress();
		String agent = getUserAgent();
		String type = Comment.TYPE_COMMENT;

		Comment comment = new Comment();

		comment.setContentModule(module);
		comment.setText(text);
		comment.setIp(ip);
		comment.setAgent(agent);
		comment.setAuthor(author);
		comment.setEmail(email);
		comment.setType(type);
		comment.setStatus(status);

		if (userId != null) {
			comment.setUserId(new BigInteger(userId));
		}

		comment.save();

		if (isAjaxRequest()) {
			renderAjaxResultForSuccess();
		} else {
			if (gotoUrl != null) {
				redirect(gotoUrl);
			}
		}
	}

	private String getIPAddress() {
		String ip = getRequest().getHeader("X-getRequest()ed-For");
		if (!StringUtils.isNotBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("X-Forwarded-For");
		}
		if (!StringUtils.isNotBlank(ip)|| "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("Proxy-Client-IP");
		}
		if (!StringUtils.isNotBlank(ip)|| "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("WL-Proxy-Client-IP");
		}
		if (!StringUtils.isNotBlank(ip)|| "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("HTTP_CLIENT_IP");
		}
		if (!StringUtils.isNotBlank(ip)|| "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (!StringUtils.isNotBlank(ip)|| "unknown".equalsIgnoreCase(ip)) {
			ip = getRequest().getRemoteAddr();
		}
		return ip;
	}

	private String getUserAgent() {
		return getRequest().getHeader("User-Agent");
	}

}
