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
package io.jpress.model;

import java.math.BigInteger;

import io.jpress.model.base.BaseComment;
import io.jpress.model.core.Table;
import io.jpress.model.query.CommentQuery;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.UserQuery;

@Table(tableName = "comment", primaryKey = "id")
public class Comment extends BaseComment<Comment> {
	private static final long serialVersionUID = 1L;

	public static final String TYPE_COMMENT = "comment";

	public static String STATUS_DELETE = "delete";
	public static String STATUS_DRAFT = "draft";
	public static String STATUS_NORMAL = "normal";

	private Content content;
	private User user;
	private Comment parent;

	public Content getContent() {
		if (content != null) {
			return content;
		}

		if (getContentId() != null) {
			content = ContentQuery.me().findById(getContentId());
		}

		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public User getUser() {
		if (user != null) {
			return user;
		}

		if (getUserId() != null) {
			user = UserQuery.me().findById(getUserId());
		}

		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Comment getParent() {
		if (parent != null) {
			return parent;
		}

		if (getContentId() != null) {
			parent = CommentQuery.me().findById(getParentId());
		}

		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public boolean isDelete() {
		return STATUS_DELETE.equals(getStatus());
	}

	public String getContentUrl() {
		BigInteger contentId = getContentId();
		if (contentId == null)
			return null;

		Content c = ContentQuery.me().findById(contentId);
		return c == null ? null : c.getUrl();
	}

	public boolean updateCommentCount() {
		long count = CommentQuery.me().findCountByParentIdInNormal(getId());
		if (count > 0) {
			setCommentCount(count);
			return this.update();
		}
		return false;
	}

	@Override
	public boolean update() {
		removeCache(getId());
		removeCache(getSlug());

		return super.update();
	}

	@Override
	public boolean delete() {
		removeCache(getId());
		removeCache(getSlug());

		return super.delete();
	}

	@Override
	public boolean save() {
		removeCache(getId());
		removeCache(getSlug());

		return super.save();
	}
}
