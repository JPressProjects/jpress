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
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class CommentListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 评论添加到数据库
		if (Actions.COMMENT_ADD.equals(message.getAction())) {
			Comment comment = message.getData();
			if (comment != null && comment.getContentId() != null) {
				Content content = ContentQuery.me().findById(comment.getContentId());
				if (content != null) {
					content.updateCommentCount();
				}
			}
		}

		// 文章被更新
		else if (Actions.COMMENT_UPDATE.equals(message.getAction())) {

		}

		// 文章被删除
		else if (Actions.COMMENT_DELETE.equals(message.getAction())) {
			Comment comment = message.getData();
			if (comment != null && comment.getContentId() != null) {
				Content content = ContentQuery.me().findById(comment.getContentId());
				if (content != null) {
					content.updateCommentCount();
				}
			}
		}
	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.COMMENT_ADD);
		messageAction.register(Actions.COMMENT_DELETE);
	}

}
