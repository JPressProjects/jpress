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

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseComment;

import java.math.BigInteger;
import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Page;

@Table(tableName = "comment", primaryKey = "id")
public class Comment extends BaseComment<Comment> {
	private static final long serialVersionUID = 1L;

	public static final String TYPE_COMMENT = "comment";
	public static String STATUS_DELETE = "delete";
	public static String STATUS_DRAFT = "draft";
	public static String STATUS_NORMAL = "normal";

	public static final Comment DAO = new Comment();

	public Page<Comment> doPaginate(int pageNumber, int pageSize, String module, String type) {

		String select = " select c.*,content.title content_title,u.username";

		StringBuilder fromBuilder = new StringBuilder("  from comment c");
		fromBuilder.append(" left join content on c.content_id = content.id");
		fromBuilder.append(" left join user u on c.user_id = u.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "c.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, " c.content_module", module, params, needWhere);

		fromBuilder.append("order by c.created");

		if (params.isEmpty()) {
			return paginate(pageNumber, pageSize, select, fromBuilder.toString());
		}
		return paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Comment> doPaginateByContentId(int pageNumber, int pageSize, BigInteger contentId) {

		String select = " select c.*,content.title content_title,u.username";
		String sqlExceptSelect = " from comment c " + "left join content on c.content_id = content.id "
				+ "left join user u on c.user_id = u.id " + "where c.content_id = ? " + "order by c.created";

		return paginate(pageNumber, pageSize, select, sqlExceptSelect, contentId);
	}

	public String getUsername() {
		return get("username");
	}

	public String getcontentTitle() {
		return get("content_title");
	}
}
