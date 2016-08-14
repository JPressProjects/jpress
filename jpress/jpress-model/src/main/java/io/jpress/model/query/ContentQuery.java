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
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.core.db.Jdb;
import io.jpress.model.Content;
import io.jpress.model.vo.Archive;
import io.jpress.template.TemplateUtils;
import io.jpress.utils.StringUtils;

public class ContentQuery extends JBaseQuery {

	private static final Content DAO = new Content();
	private static final ContentQuery QUERY = new ContentQuery();

	public static ContentQuery me() {
		return QUERY;
	}

	public boolean deleteById(BigInteger id) {
		return DAO.deleteById(id);
	}

	public Page<Content> paginateByMetadata(int page, int pagesize, String meta_key, String meta_value) {
		StringBuilder sqlBuilder = new StringBuilder(" FROM ");
		sqlBuilder.append(" ( ");
		sqlBuilder.append(
				" select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys ");
		sqlBuilder.append(
				" GROUP_CONCAT(m.id ,':',m.meta_key,':',m.meta_value SEPARATOR ',') metadatas , u.username ,u.nickname,u.avatar");
		sqlBuilder.append(" FROM content c ");
		sqlBuilder.append(" left join mapping m on c.id = m.`content_id` ");
		sqlBuilder.append(" left join taxonomy  t on m.`taxonomy_id` = t.id ");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		sqlBuilder.append(" left join metadata md on c.id = md.`object_id` and md.`object_type`='content' ");
		sqlBuilder.append(" where c.`metadatas` like ? ");
		sqlBuilder.append(" GROUP BY c.id ");
		sqlBuilder.append(" ORDER BY c.created DESC ");
		sqlBuilder.append(" ) ");
		sqlBuilder.append(" c ");

		return DAO.paginate(page, pagesize, true, sqlBuilder.toString(), "%:" + meta_key + ":" + meta_value);
	}

	public Page<Content> paginateByModule(int page, int pagesize, String module) {
		return paginate(page, pagesize, module, null, null, null, null, null);
	}

	public Page<Content> paginateByModuleAndStatus(int page, int pagesize, String module, String status,
			String orderBy) {
		return paginate(page, pagesize, module, null, status, null, null, orderBy);
	}

	public Page<Content> paginateByModuleAndStatus(int page, int pagesize, String module, String status) {
		return paginate(page, pagesize, module, null, status, null, null, null);
	}

	public Page<Content> paginateBySearch(int page, int pagesize, String module, String keyword, String status,
			BigInteger[] tids, String month) {
		String[] modules = StringUtils.isNotBlank(module) ? new String[] { module } : null;
		return paginate(page, pagesize, modules, keyword, status, tids, null, month, null);
	}

	public Page<Content> paginateByModuleInNormal(int page, int pagesize, String module) {
		return paginate(page, pagesize, module, null, Content.STATUS_NORMAL, null, null, null);
	}

	public Page<Content> paginateByModuleNotInDelete(int page, int pagesize, String module, String keyword,
			BigInteger[] taxonomyIds, String month) {
		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username,u.nickname,u.avatar";

		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");
		fromBuilder.append(" where c.status <> ?");

		LinkedList<Object> params = new LinkedList<Object>();
		params.add(Content.STATUS_DELETE);

		boolean needWhere = false;
		needWhere = appendIfNotEmpty(fromBuilder, "c.module", module, params, needWhere);

		if (StringUtils.isNotBlank(keyword)) {
			fromBuilder.append(" AND c.title like ? ");
			params.add("%" + keyword + "%");
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			fromBuilder.append(" AND t.id in " + toString(taxonomyIds));
		}

		if (StringUtils.isNotBlank(month)) {
			fromBuilder.append(" DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		fromBuilder.append(" group by c.id");
		fromBuilder.append(" ORDER BY c.created DESC");

		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, fromBuilder.toString());
		}

		return DAO.paginate(page, pagesize, true, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Content> paginate(int page, int pagesize, String module, String keyword, String status,
			BigInteger[] taxonomyIds, BigInteger userId, String orderBy) {

		String[] modules = StringUtils.isNotBlank(module) ? new String[] { module } : null;

		return paginate(page, pagesize, modules, keyword, status, taxonomyIds, userId, null, orderBy);
	}

	public Page<Content> paginate(int page, int pagesize, String[] modules, String keyword, String status,
			BigInteger[] taxonomyIds, BigInteger userId, String month, String orderBy) {

		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username,u.nickname,u.avatar";

		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "c.module", modules, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "u.id", userId, params, needWhere);

		if (StringUtils.isNotBlank(keyword)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" c.title like ? ");
			params.add("%" + keyword + "%");
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" t.id in " + toString(taxonomyIds));
		}

		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(fromBuilder, needWhere);
			fromBuilder.append(" DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		fromBuilder.append(" group by c.id");

		buildOrderBy(orderBy, fromBuilder);

		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, fromBuilder.toString());
		}

		return DAO.paginate(page, pagesize, true, select, fromBuilder.toString(), params.toArray());
	}

	private String toString(Object[] a) {

		int iMax = a.length - 1;

		StringBuilder b = new StringBuilder();
		b.append('(');
		for (int i = 0;; i++) {
			b.append(String.valueOf(a[i]));
			if (i == iMax)
				return b.append(')').toString();
			b.append(", ");
		}
	}

	private void buildOrderBy(String orderBy, StringBuilder fromBuilder) {

		if (StringUtils.isBlank(orderBy)) {
			fromBuilder.append(" ORDER BY c.created DESC");
			return;
		}

		// maybe orderby == "view_count desc";
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];

		if ("view_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.view_count ");
		}

		else if ("comment_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.comment_count ");
		}

		else if ("modified".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.modified ");
		}

		else if ("vote_up".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.vote_up ");
		}

		else if ("vote_down".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.vote_down ");
		}

		else if ("order_number".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.order_number ");
		}

		else if ("parent_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.parent_id ");
		}

		else if ("object_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.object_id ");
		}

		else {
			fromBuilder.append(" ORDER BY c.created ");
		}

		if (orderbyInfo.length == 1) {
			fromBuilder.append(" DESC ");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}

	}

	public Long findCountByModuleAndStatus(String module, String status) {
		return DAO.doFindCount("module = ? and status=?", module, status);
	}

	public List<Content> findListInNormal(int page, int pagesize) {
		return findListInNormal(page, pagesize, null, null, null, null, null, null, null, null, null, null, null, null,
				null);
	}

	public List<Content> findListInNormal(int page, int pagesize, String module) {
		String[] modules = new String[] { module };
		return findListInNormal(page, pagesize, null, null, null, null, modules, null, null, null, null, null, null,
				null, null);
	}

	public List<Content> findListInNormal(int page, int pagesize, BigInteger taxonomyId) {
		return findListInNormal(page, pagesize, null, null, new BigInteger[] { taxonomyId }, null, null, null, null,
				null, null, null, null, null, null);
	}

	/**
	 * @param page
	 * @param pagesize
	 * @param orderBy
	 * @param keyword
	 * @param typeIds
	 * @param typeSlugs
	 * @param modules
	 * @param styles
	 * @param slugs
	 * @param userIds
	 * @param parentIds
	 * @param tags
	 * @return
	 */
	public List<Content> findListInNormal(int page, int pagesize, String orderBy, String keyword, BigInteger[] typeIds,
			String[] typeSlugs, String[] modules, String[] styles, String[] flags, String[] slugs, BigInteger[] userIds,
			BigInteger[] parentIds, String[] tags, Boolean hasThumbnail, String month) {

		if (modules == null) {
			modules = TemplateUtils.getCurrentTemplateModulesAsArray();
		}

		StringBuilder sqlBuilder = getBaseSelectSql();
		sqlBuilder.append(" where c.status = 'normal' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(sqlBuilder, "m.taxonomy_id", typeIds, params, false);
		appendIfNotEmpty(sqlBuilder, "c.module", modules, params, false);
		appendIfNotEmpty(sqlBuilder, "c.style", styles, params, false);
		appendIfNotEmpty(sqlBuilder, "c.slug", slugs, params, false);
		appendIfNotEmpty(sqlBuilder, "c.user_id", userIds, params, false);
		appendIfNotEmpty(sqlBuilder, "c.parent_id", parentIds, params, false);
		appendIfNotEmpty(sqlBuilder, "t.slug", typeSlugs, params, false);
		appendIfNotEmptyWithLike(sqlBuilder, "c.flag", flags, params, false);

		if (null != tags && tags.length > 0) {
			appendIfNotEmpty(sqlBuilder, "t.title", tags, params, false);
			sqlBuilder.append(" AND t.`type`='tag' ");
		}

		if (StringUtils.isNotBlank(keyword)) {
			sqlBuilder.append(" AND c.title like ?");
			params.add("%" + keyword + "%");
		}

		if (StringUtils.isNotBlank(month)) {
			sqlBuilder.append(" AND DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		if (null != hasThumbnail) {
			if (hasThumbnail) {
				sqlBuilder.append(" AND c.thumbnail is not null ");
			} else {
				sqlBuilder.append(" AND c.thumbnail is null ");
			}
		}

		sqlBuilder.append("GROUP BY c.id");

		buildOrderBy(orderBy, sqlBuilder);

		sqlBuilder.append(" LIMIT ?, ?");
		params.add(page - 1);
		params.add(pagesize);

		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Content> findByModule(String module) {
		return DAO.doFind("module = ? ", module);
	}

	public List<Content> findByModuleAndTitle(String module, String title, int limit) {
		return DAO.doFind("module = ? and title = ? order by id desc limit ?", module, title, limit);
	}

	public Content findFirstByModuleAndTitle(String module, String title) {
		return DAO.doFindFirst("module = ? and title = ? order by id desc", module, title);
	}

	public Content findFirstByModuleAndText(String module, String text) {
		return DAO.doFindFirst("module = ? and text = ? order by id desc", module, text);
	}

	public Content findFirstByModuleAndObjectId(String module, BigInteger objectId) {
		return DAO.doFindFirst("module = ? and object_id = ? order by id desc", module, objectId);
	}

	public List<Content> searchByModuleAndTitle(String module, String title, int limit) {
		return DAO.doFind("module = ? and title like ? order by id desc limit ?", module, "%" + title + "%", limit);
	}

	public List<Content> findByModule(String module, String orderby) {
		StringBuilder sqlBuilder = new StringBuilder("select * from content c");
		sqlBuilder.append(" where module = ? ");
		buildOrderBy(orderby, sqlBuilder);
		return DAO.find(sqlBuilder.toString(), module);
	}

	public List<Content> findArchiveByModule(String module) {
		StringBuilder sqlBuilder = getBaseSelectSql("DATE_FORMAT( c.created, \"%Y-%m\" ) as archiveDate");
		sqlBuilder.append(" where module = ? ");
		sqlBuilder.append(" order by c.created DESC");
		return DAO.find(sqlBuilder.toString(), module);
	}

	public List<Content> findByModule(String module, BigInteger parentId, String orderby) {
		StringBuilder sqlBuilder = new StringBuilder("select * from content c");
		sqlBuilder.append(" where module = ? ");
		sqlBuilder.append(" AND  parent_id = ? ");
		buildOrderBy(orderby, sqlBuilder);
		return DAO.find(sqlBuilder.toString(), module, parentId);
	}

	public Content findBySlug(final String slug) {
		final StringBuilder sql = getBaseSelectSql();

		sql.append(" WHERE c.slug = ?");
		sql.append(" GROUP BY c.id");

		return DAO.getCache(slug, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findFirst(sql.toString(), slug);
			}
		});
	}

	public Content findById(final BigInteger id) {
		final StringBuilder sql = getBaseSelectSql();
		sql.append(" WHERE c.id = ?");
		sql.append(" GROUP BY c.id");

		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findFirst(sql.toString(), id);
			}
		});
	}

	public Content findNext(final Content currentContent) {
		final StringBuilder sqlBuilder = new StringBuilder(" select ");
		sqlBuilder.append(" c.*,u.username,u.nickname,u.avatar ");
		sqlBuilder.append(" from content c");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		sqlBuilder.append(" WHERE c.id > ?");
		sqlBuilder.append(" AND c.module = ?");
		sqlBuilder.append(" AND c.status = 'normal'");
		sqlBuilder.append(" ORDER BY c.created ASC");
		sqlBuilder.append(" LIMIT 1");

		return DAO.getTemp(String.format("next_%s_$s", currentContent.getId(), currentContent.getModule()),
				new IDataLoader() {
					@Override
					public Object load() {
						return DAO.findFirst(sqlBuilder.toString(), currentContent.getId(), currentContent.getModule());
					}
				});
	}

	public Content findPrevious(final Content currentContent) {
		final StringBuilder sqlBuilder = new StringBuilder(" select ");
		sqlBuilder.append(" c.*,u.username,u.nickname,u.avatar ");
		sqlBuilder.append(" from content c");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		sqlBuilder.append(" WHERE c.id < ?");
		sqlBuilder.append(" AND c.module = ?");
		sqlBuilder.append(" AND c.status = 'normal'");
		sqlBuilder.append(" ORDER BY c.created DESC");
		sqlBuilder.append(" LIMIT 1");

		return DAO.getTemp(String.format("previous_%s_$s", currentContent.getId(), currentContent.getModule()),
				new IDataLoader() {
					@Override
					public Object load() {
						return DAO.findFirst(sqlBuilder.toString(), currentContent.getId(), currentContent.getModule());
					}
				});
	}

	private StringBuilder getBaseSelectSql() {
		return getBaseSelectSql(null);
	}

	private StringBuilder getBaseSelectSql(String columns) {
		StringBuilder sqlBuilder = new StringBuilder(" select ");
		sqlBuilder.append(" c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys ");
		sqlBuilder.append(" ,u.username,u.nickname,u.avatar ");
		if (StringUtils.isNotBlank(columns)) {
			sqlBuilder.append(",").append(columns);
		}
		sqlBuilder.append(" from content c");
		sqlBuilder.append(" left join mapping m on c.id = m.`content_id`");
		sqlBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		sqlBuilder.append(" left join user u on c.user_id = u.id ");
		return sqlBuilder;
	}

	public Content findById(Object idValue) {
		return DAO.findFirst(getBaseSelectSql() + " WHERE c.id=? ", idValue);
	}

	public long findCountByModule(String module) {
		return DAO.doFindCount("module = ?", module);
	}

	public long findCountInNormalByModule(String module) {
		return DAO.doFindCount("module = ? AND status <> ?", module, Content.STATUS_DELETE);
	}

	public long findCountInNormalByModuleAndUserId(String module, BigInteger userId) {
		return DAO.doFindCount("module = ? AND status <> ? and user_id = ? ", module, Content.STATUS_DELETE, userId);
	}

	public long findCountInNormalByParentId(BigInteger id, String module) {
		if (id == null) {
			return DAO.doFindCount("parent_id is null AND module = ? AND status <> ?", module, Content.STATUS_DELETE);
		}
		return DAO.doFindCount("parent_id = ? AND module = ? AND status <> ?", id, module, Content.STATUS_DELETE);
	}

	public int batchTrash(BigInteger... ids) {
		if (ids != null && ids.length > 0) {
			List<Object> params = new LinkedList<Object>();
			StringBuilder sb = new StringBuilder("UPDATE content SET status=? ");
			params.add(Content.STATUS_DELETE);
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					sb.append(" WHERE id = ? ");
				} else {
					sb.append(" OR id = ? ");
				}
				params.add(ids[i]);
			}
			return Jdb.update(sb.toString(), params.toArray());
		}
		return 0;
	}

	public int batchDelete(BigInteger... ids) {
		if (ids != null && ids.length > 0) {
			List<Object> params = new LinkedList<Object>();
			StringBuilder sb = new StringBuilder("DELETE FROM content ");
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					sb.append(" WHERE id = ? ");
				} else {
					sb.append(" OR id = ? ");
				}
				params.add(ids[i]);
			}
			return Jdb.update(sb.toString(), params.toArray());
		}
		return 0;
	}

	public List<Archive> findArchives(String module) {
		String sql = "SELECT DATE_FORMAT( c.created, \"%Y-%m\" ) as d, COUNT( * ) count FROM content c"
				+ " WHERE c.module = ? GROUP BY d";
		List<Record> list = Jdb.find(sql, module);
		if (list == null || list.isEmpty())
			return null;

		List<Archive> datas = new ArrayList<Archive>();
		for (Record r : list) {
			String date = r.getStr("d");
			if (StringUtils.isNotBlank(date)) {
				datas.add(new Archive(date, r.getLong("count")));
			}
		}
		return datas;
	}

}
