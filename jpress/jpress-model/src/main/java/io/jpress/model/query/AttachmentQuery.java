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

import io.jpress.core.db.Jdb;
import io.jpress.model.Attachment;
import io.jpress.model.vo.Archive;
import io.jpress.utils.StringUtils;

public class AttachmentQuery extends JBaseQuery {

	private static final Attachment DAO = new Attachment();
	private static final AttachmentQuery QUERY = new AttachmentQuery();

	public static AttachmentQuery me() {
		return QUERY;
	}

	public Page<Attachment> paginate(int pageNumber, int pageSize, String keyword, String month, String mime) {

		StringBuilder fromBuilder = new StringBuilder(" FROM attachment a ");
		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;

		if (StringUtils.isNotBlank(keyword)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" a.title like ? ");
			params.add("%"+keyword + "%");
		}
		
		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" DATE_FORMAT( a.created, \"%Y-%m\" ) = ? ");
			params.add(month);
		}

		if (StringUtils.isNotBlank(mime)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" a.mime_type like ? ");
			params.add(mime + "%");
		}
		
		
		fromBuilder.append(" ORDER BY a.created DESC ");

		if (params.isEmpty()) {
			return DAO.paginate(pageNumber, pageSize, "SELECT * ", fromBuilder.toString());
		} else {
			return DAO.paginate(pageNumber, pageSize, "SELECT * ", fromBuilder.toString(), params.toArray());
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
