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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import io.jpress.model.Attachment;
import io.jpress.model.core.Jdb;
import io.jpress.model.vo.Archive;
import io.jpress.utils.StringUtils;

public class AttachmentQuery extends JBaseQuery {

	protected static final Attachment DAO = new Attachment();
	private static final AttachmentQuery QUERY = new AttachmentQuery();

	public static AttachmentQuery me() {
		return QUERY;
	}

	public Page<Attachment> paginate(int pageNumber, int pageSize, BigInteger userId, BigInteger contentId, String type,
			String flag, String keyword, String month, String mime, String orderBy) {

		StringBuilder fromBuilder = new StringBuilder(" FROM attachment a ");
		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "a.user_id", userId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "a.content_id", contentId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "a.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "a.`flag`", flag, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, " a.title", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, " a.mime_type", mime, params, needWhere);

		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" DATE_FORMAT( a.created, \"%Y-%m\" ) = ? ");
			params.add(month);
		}

		buildOrderBy(orderBy, fromBuilder);

		if (params.isEmpty()) {
			return DAO.paginate(pageNumber, pageSize, "SELECT * ", fromBuilder.toString());
		} else {
			return DAO.paginate(pageNumber, pageSize, "SELECT * ", fromBuilder.toString(), params.toArray());
		}

	}

	public List<Attachment> findList(BigInteger userId, BigInteger contentId, String type, String flag, String keyword,
			String month, String mime, String orderBy) {

		StringBuilder sqlBuilder = new StringBuilder("SELECT *  FROM attachment a ");
		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;

		needWhere = appendIfNotEmpty(sqlBuilder, "a.user_id", userId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.content_id", contentId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.`flag`", flag, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(sqlBuilder, " a.title", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(sqlBuilder, " a.mime_type", mime, params, needWhere);

		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(sqlBuilder, needWhere);
			sqlBuilder.append(" DATE_FORMAT( a.created, \"%Y-%m\" ) = ? ");
			params.add(month);
		}

		buildOrderBy(orderBy, sqlBuilder);

		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		} else {
			return DAO.find(sqlBuilder.toString(), params.toArray());
		}

	}

	public Attachment findFirst(BigInteger userId, BigInteger contentId, String type, String flag, String keyword,
			String month, String mime, String orderBy) {

		StringBuilder sqlBuilder = new StringBuilder("SELECT *  FROM attachment a ");
		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;

		needWhere = appendIfNotEmpty(sqlBuilder, "a.user_id", userId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.content_id", contentId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "a.`flag`", flag, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(sqlBuilder, " a.title", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(sqlBuilder, " a.mime_type", mime, params, needWhere);

		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(sqlBuilder, needWhere);
			sqlBuilder.append(" DATE_FORMAT( a.created, \"%Y-%m\" ) = ? ");
			params.add(month);
		}

		buildOrderBy(orderBy, sqlBuilder);

		if (params.isEmpty()) {
			return DAO.findFirst(sqlBuilder.toString());
		} else {
			return DAO.findFirst(sqlBuilder.toString(), params.toArray());
		}

	}

	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {

		if (StringUtils.isBlank(orderBy)) {
			fromBuilder.append(" ORDER BY a.created DESC");
			return;
		}

		// maybe orderby == "order_number desc";
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];

		if ("id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY a.id ");
		}

		else if ("user_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY a.user_id ");
		}

		else if ("content_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY a.content_id ");
		}

		else if ("order_number".equals(orderBy)) {
			fromBuilder.append(" ORDER BY a.order_number ");
		}

		else {
			fromBuilder.append(" ORDER BY a.created ");
		}

		if (orderbyInfo.length == 1) {
			fromBuilder.append(" DESC ");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}

	}

	public Attachment findById(BigInteger id) {
		return DAO.findById(id);
	}

	public List<Archive> findArchives() {
		String sql = "SELECT DATE_FORMAT( created, \"%Y-%m\" ) as d, COUNT( * ) c FROM attachment GROUP BY d";
		List<Record> list = Jdb.find(sql);
		if (list == null || list.isEmpty())
			return null;

		List<Archive> datas = new ArrayList<Archive>();
		for (Record r : list) {
			String date = r.getStr("d");
			if (StringUtils.isNotBlank(date)) {
				datas.add(new Archive(date, r.getLong("c")));
			}
		}

		return datas;
	}

}
