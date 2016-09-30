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

import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;
import io.jpress.model.Comment;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;

@Listener(action = { Comment.ACTION_ADD, Comment.ACTION_UPDATE, Comment.ACTION_DELETE })
public class UserCommentCountUpdateListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 评论添加到数据库
		if (Comment.ACTION_ADD.equals(message.getAction())) {
			updateUserCommentCount(message);
		}

		// 文章被更新
		else if (Comment.ACTION_UPDATE.equals(message.getAction())) {
			updateUserCommentCount(message);
		}

		// 文章被删除
		else if (Comment.ACTION_DELETE.equals(message.getAction())) {
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
