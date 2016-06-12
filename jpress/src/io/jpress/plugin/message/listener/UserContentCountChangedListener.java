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
package io.jpress.plugin.message.listener;

import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class UserContentCountChangedListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 文章添加到数据库
		if (Actions.CONTENT_ADD.equals(message.getAction())) {
			updateUserConentCount(message);
		}

		// 文章被更新
		else if (Actions.CONTENT_UPDATE.equals(message.getAction())) {
			updateUserConentCount(message);
		}

		// 文章被删除
		else if (Actions.CONTENT_DELETE.equals(message.getAction())) {
			updateUserConentCount(message);
		}
	}

	private void updateUserConentCount(Message message) {
		Object temp = message.getData();
		if (temp != null && temp instanceof Content) {
			Content content = (Content) temp;
			if (Content.STATUS_NORMAL.equals(content.getStatus()) && content.getUserId() != null) {
				User user = User.DAO.findById(content.getUserId());
				if (user != null)
					user.updateContentCount();
			}
		}
	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.CONTENT_ADD);
		messageAction.register(Actions.CONTENT_UPDATE);
		messageAction.register(Actions.CONTENT_DELETE);
	}

}
