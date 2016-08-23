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

import io.jpress.model.Comment;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.plugin.message.Actions;
import io.jpress.plugin.message.Listener;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageListener;

@Listener(action = { Actions.COMMENT_ADD, Actions.COMMENT_UPDATE, Actions.COMMENT_DELETE })
public class UserCommentCountUpdateListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 评论添加到数据库
		if (Actions.COMMENT_ADD.equals(message.getAction())) {
			updateUserCommentCount(message);
		}

		// 文章被更新
		else if (Actions.COMMENT_UPDATE.equals(message.getAction())) {
			updateUserCommentCount(message);
		}

		// 文章被删除
		else if (Actions.COMMENT_DELETE.equals(message.getAction())) {
			updateUserCommentCount(message);
		}
	}

	private void updateUserCommentCount(Message message) {
		Object temp = message.getData();
		if (temp != null && temp instanceof Comment) {
			Comment comment = (Comment) temp;
			if (Comment.STATUS_NORMAL.equals(comment.getStatus()) && comment.getUserId() != null) {
				User user = UserQuery.me().findById(comment.getUserId());
				if (user != null)
					UserQuery.me().updateCommentCount(user);
			}
		}
	}


}
