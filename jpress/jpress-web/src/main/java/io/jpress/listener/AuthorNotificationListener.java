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
package io.jpress.listener;

import java.math.BigInteger;

import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.notify.email.Email;
import io.jpress.notify.email.EmailSenderFactory;
import io.jpress.notify.sms.SmsMessage;
import io.jpress.notify.sms.SmsSenderFactory;
import io.jpress.plugin.message.Actions;
import io.jpress.plugin.message.BaseMessageListener;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.utils.StringUtils;

public class AuthorNotificationListener extends BaseMessageListener {

	@Override
	public void onMessage(Message message) {

		// 评论添加到数据库
		if (Actions.COMMENT_ADD.equals(message.getAction())) {
			notify(message);
		}

	}

	private void notify(Message message) {
		Object temp = message.getData();
		if (temp == null && !(temp instanceof Comment)) {
			return;
		}
		
		Comment comment = (Comment) temp;
		if (Comment.STATUS_NORMAL.equals(comment.getStatus()) && comment.getContentId() != null) {
			Content content = ContentQuery.me().me().findById(comment.getContentId());
			if (content != null) {
				BigInteger authorId = content.getUserId();
				notifyAuthor(authorId);
			}
		}
	}

	private void notifyAuthor(BigInteger id) {
		notifyByEmail(id);
//		notifyBySms(id);
	}

	private void notifyBySms(BigInteger id) {
		Boolean notify = OptionQuery.me().findValueAsBool("notify_author_by_sms_when_has_comment");
		if (notify != null && notify == true) {
			User user = UserQuery.me().findById(id);
			if (user == null || user.getPhone() == null) {
				return;
			}

			String content = OptionQuery.me().findValue("notify_author_content_by_sms_when_has_comment");
			if (!StringUtils.isNotBlank(content)) {
				return;
			}

			SmsMessage sms = new SmsMessage();

			sms.setContent(content);
			sms.setRec_num(user.getPhone());
			sms.setTemplate(content);
//			sms.setParam("{\"code\":\"8888\",\"product\":\"JPress\",\"customer\":\"杨福海\"}");
//			sms.setSign_name("登录验证");

			SmsSenderFactory.createSender().send(sms);

		}
	}

	private void notifyByEmail(BigInteger id) {
		Boolean notify = OptionQuery.me().findValueAsBool("notify_author_by_email_when_has_comment");
		if (notify != null && notify == true) {
			User user = UserQuery.me().findById(id);
			if (user == null || user.getEmail() == null) {
				return;
			}

			Email email = new Email();
			email.subject("有人评论了您的文章！");

			String content = OptionQuery.me().findValue("notify_author_content_by_email_when_has_comment");
			if (!StringUtils.isNotBlank(content)) {
				content = "有人评论了您的文章！";
			}
			email.content(content);
			email.to(user.getEmail());

			EmailSenderFactory.createSender().send(email);
		}
	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.COMMENT_ADD);
	}

}
