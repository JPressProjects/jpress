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
package io.jpress.model.query;

import java.math.BigInteger;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.Comment;
import io.jpress.utils.StringUtils;

public class CommentQuery extends JBaseQuery {

	private static final Comment MODEL = new Comment();

	public static Page<Comment> paginateWithContent(int pageNumber, int pageSize, String module, String type,
			BigInteger contentId, String status) {

		String select = " select c.*,content.title content_title,u.username,u.nickname";
		StringBuilder fromBuilder = new StringBuilder("  from comment c");
		fromBuilder.append(" left join content on c.content_id = content.id");
		fromBuilder.append(" left join user u on c.user_id = u.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "c.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " c.content_module", module, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " c.`status`", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " content.id", contentId, params, needWhere);

		fromBuilder.append("order by c.created desc");

		if (params.isEmpty()) {
			return MODEL.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		}
		return MODEL.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public static Page<Comment> paginateWithContentNotInDelete(int pageNumber, int pageSize, String module) {

		String select = " select c.*,content.title content_title,u.username,u.nickname";
		StringBuilder fromBuilder = new StringBuilder("  from comment c");
		fromBuilder.append(" left join content on c.content_id = content.id");
		fromBuilder.append(" left join user u on c.user_id = u.id ");
		fromBuilder.append(" where c.status <> ?");

		if (StringUtils.isNotBlank(module)) {
			fromBuilder.append(" and c.content_module = ?");
		}
		fromBuilder.append("order by c.created desc");

		if (StringUtils.isNotBlank(module)) {
			return MODEL.paginate(pageNumber, pageSize, select, fromBuilder.toString(), module, Comment.STATUS_DELETE);
		} else {
			return MODEL.paginate(pageNumber, pageSize, select, fromBuilder.toString(), Comment.STATUS_DELETE);
		}

	}

	public static Page<Comment> paginateByContentId(int pageNumber, int pageSize, BigInteger contentId) {
		return paginateWithContent(pageNumber, pageSize, null, null, contentId, Comment.STATUS_NORMAL);
	}

	public static long findCountByContentIdInNormal(BigInteger contentId) {
		return findCountByContentId(contentId, Comment.STATUS_NORMAL);
	}

	public static long findCountByContentId(BigInteger contentId, String status) {
		return MODEL.doFindCount(" content_id = ? and status=? ", contentId, status);
	}

	public static long findCountByUserIdInNormal(BigInteger userId) {
		return findCountByUserId(userId, Comment.STATUS_NORMAL);
	}

	public static long findCountByUserId(BigInteger userId, String status) {
		return MODEL.doFindCount(" user_id = ? and status=? ", userId, status);
	}

	public static Comment findById(Object idValue) {
		StringBuilder sqlBuilder = new StringBuilder("select c.*,content.title content_title,u.username,u.nickname");
		sqlBuilder.append(" from comment c");
		sqlBuilder.append(" left join content on c.content_id = content.id");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		sqlBuilder.append(" where c.id = ?");

		return MODEL.findFirst(sqlBuilder.toString(), idValue);
	}

	public static long findCountByModule(String module) {
		return MODEL.doFindCount("content_module = ?", module);
	}

	public static long findCountInNormalByModule(String module) {
		return MODEL.doFindCount("content_module = ? AND status <> ?", module, Comment.STATUS_DELETE);
	}

	public static long findCountByModuleAndStatus(String module, String status) {
		return MODEL.doFindCount("content_module = ? and status=?", module, status);
	}

}
