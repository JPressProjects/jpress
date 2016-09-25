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
import io.jpress.model.Content;
import io.jpress.model.query.CommentQuery;
import io.jpress.model.query.ContentQuery;

@Listener(action = { Comment.ACTION_ADD, Comment.ACTION_UPDATE, Comment.ACTION_DELETE })
public class CommentListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 有新评论
		if (Comment.ACTION_ADD.equals(message.getAction())) {
			updateContentCommentCount(message);
			updateCommentCount(message);
		}

		// 评论被更新（可能状态呗更新）
		else if (Comment.ACTION_UPDATE.equals(message.getAction())) {
			updateContentCommentCount(message);
			updateCommentCount(message);
		}

		// 评论被删除
		else if (Comment.ACTION_DELETE.equals(message.getAction())) {
			updateContentCommentCount(message);
			updateCommentCount(message);
		}
	}

	/**
	 * 更新文章评论数量
	 * 
	 * @param message
	 */
	private void updateContentCommentCount(Message message) {
		Comment comment = message.getData();
		if (comment != null && comment.getContentId() != null) {
			Content content = ContentQuery.me().findById(comment.getContentId());
			if (content != null) {
				content.updateCommentCount();
			}
		}
	}

	/**
	 * 更新评论的回复数量
	 * 
	 * @param message
	 */
	private void updateCommentCount(Message message) {
		Comment comment = message.getData();
		if (comment != null && comment.getParentId() != null) {
			Comment parentComment = CommentQuery.me().findById(comment.getParentId());
			if (parentComment != null) {
				parentComment.updateCommentCount();
			}
		}
	}

}
