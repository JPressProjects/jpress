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

	private static final Comment DAO = new Comment();
	private static final CommentQuery QUERY = new CommentQuery();

	public static CommentQuery me() {
		return QUERY;
	}

	public Page<Comment> paginateWithContent(int pageNumber, int pageSize, String module, String type,
			BigInteger contentId, String status) {

		String select = " select c.*,content.title content_title,u.username,u.nickname,"
				+ "quote_comment.text qc_content,quote_comment.author qc_author,"
				+ "quote_user.username qc_username,quote_user.nickname qc_nickname";
		
		StringBuilder fromBuilder = new StringBuilder("  from comment c");
		fromBuilder.append(" left join content on c.content_id = content.id");
		fromBuilder.append(" left join user u on c.user_id = u.id ");
		fromBuilder.append(" left join comment quote_comment on c.parent_id = quote_comment.id");
		fromBuilder.append(" left join user quote_user on quote_comment.user_id = quote_user.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "c.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " c.content_module", module, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " c.`status`", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " content.id", contentId, params, needWhere);

		fromBuilder.append("order by c.created desc");

		if (params.isEmpty()) {
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		}
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Comment> paginateWithContentNotInDelete(int pageNumber, int pageSize, String module) {

		String select = " select c.*,content.title content_title,u.username,u.nickname";
		StringBuilder fromBuilder = new StringBuilder("  from comment c");
		fromBuilder.append(" left join content on c.content_id = content.id");
		fromBuilder.append(" left join user u on c.user_id = u.id ");
		fromBuilder.append(" where c.status <> ?");

		if (StringUtils.isNotBlank(module)) {
			fromBuilder.append(" and c.content_module = ? ");
		}
		fromBuilder.append(" order by c.created desc");

		if (StringUtils.isNotBlank(module)) {
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), Comment.STATUS_DELETE, module);
		} else {
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), Comment.STATUS_DELETE);
		}

	}

	public Page<Comment> paginateByContentId(int pageNumber, int pageSize, BigInteger contentId) {
		return paginateWithContent(pageNumber, pageSize, null, null, contentId, Comment.STATUS_NORMAL);
	}

	public long findCountByContentIdInNormal(BigInteger contentId) {
		return findCountByContentId(contentId, Comment.STATUS_NORMAL);
	}

	public long findCountByContentId(BigInteger contentId, String status) {
		return DAO.doFindCount(" content_id = ? and status=? ", contentId, status);
	}
	
	public long findCountByParentIdInNormal(BigInteger pId) {
		return findCountByContentId(pId, Comment.STATUS_NORMAL);
	}
	
	public long findCountByParentId(BigInteger pId, String status) {
		return DAO.doFindCount(" parent_id = ? and status=? ", pId, status);
	}

	public long findCountByUserIdInNormal(BigInteger userId) {
		return findCountByUserId(userId, Comment.STATUS_NORMAL);
	}

	public long findCountByUserId(BigInteger userId, String status) {
		return DAO.doFindCount(" user_id = ? and status=? ", userId, status);
	}

	public Comment findById(Object idValue) {
		StringBuilder sqlBuilder = new StringBuilder("select c.*,content.title content_title,u.username,u.nickname");
		sqlBuilder.append(" from comment c");
		sqlBuilder.append(" left join content on c.content_id = content.id");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		sqlBuilder.append(" where c.id = ?");

		return DAO.findFirst(sqlBuilder.toString(), idValue);
	}

	public long findCountByModule(String module) {
		return DAO.doFindCount("content_module = ?", module);
	}

	public long findCountInNormalByModule(String module) {
		return DAO.doFindCount("content_module = ? AND status <> ?", module, Comment.STATUS_DELETE);
	}

	public long findCountByModuleAndStatus(String module, String status) {
		return DAO.doFindCount("content_module = ? and status=?", module, status);
	}

}
