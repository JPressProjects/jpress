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

import io.jpress.model.User;
import io.jpress.model.query.OptionQuery;
import io.jpress.notify.email.Email;
import io.jpress.notify.email.EmailSenderFactory;
import io.jpress.plugin.message.Actions;
import io.jpress.plugin.message.BaseMessageListener;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.utils.StringUtils;

public class AdminNotificationListener extends BaseMessageListener {

	@Override
	public void onMessage(Message message) {
		// 用用户注册了
		if (Actions.USER_CREATED.equals(message.getAction())) {
			notify(message);
		}

	}

	private void notify(Message message) {
		Object temp = message.getData();
		if (temp == null && !(temp instanceof User)) {
			return;
		}

		User user = (User) temp;
		notifyAuthor(user);
	}

	private void notifyAuthor(User registedUser) {
		notifyByEmail(registedUser);
		// notifyBySms(id);
	}

	private void notifyByEmail(User registedUser) {
		Boolean notify = OptionQuery.me().findValueAsBool("notify_admin_by_email_when_user_registed");
		if (notify != null && notify == true) {
			
			String toemail = OptionQuery.me().findValue("web_administrator_email");
			if(!StringUtils.isNotBlank(toemail)){
				return;
			}

			Email email = new Email();
			email.subject("您的网站有人注册了！");

			String content = OptionQuery.me().findValue("notify_admin_by_content_email_when_user_registed");
			
			System.err.println("======content:"+content);
			
			if (!StringUtils.isNotBlank(content)) {
				content = "您的网站有人注册了！";
			}
			email.content(content);
			email.to(toemail);

			EmailSenderFactory.createSender().send(email);
		}
	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.USER_CREATED);
	}

}
