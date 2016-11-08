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

import io.jpress.model.Content;
import io.jpress.model.core.Jdb;
import io.jpress.model.vo.Archive;
import io.jpress.template.TemplateManager;
import io.jpress.utils.StringUtils;

public class ContentQuery extends JBaseQuery {

	protected static final Content DAO = new Content();
	private static final ContentQuery QUERY = new ContentQuery();

	public static ContentQuery me() {
		return QUERY;
	}

	public boolean deleteById(BigInteger id) {
		Content c = findById(id);
		if (c != null)
			return c.delete();
		return false;
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

		StringBuilder sql = new StringBuilder(" from content c");
		sql.append(" left join mapping m on c.id = m.`content_id`");
		sql.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		sql.append(" where c.status <> ?");

		LinkedList<Object> params = new LinkedList<Object>();
		params.add(Content.STATUS_DELETE);

		appendIfNotEmpty(sql, "c.module", module, params, false);

		if (StringUtils.isNotBlank(keyword)) {
			sql.append(" AND c.title like ? ");
			params.add("%" + keyword + "%");
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			sql.append(" AND t.id in " + toString(taxonomyIds));
		}

		if (StringUtils.isNotBlank(month)) {
			sql.append(" DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		sql.append(" group by c.id");
		sql.append(" ORDER BY c.created DESC");

		String select = "select c.*";
		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, sql.toString());
		}

		return DAO.paginate(page, pagesize, true, select, sql.toString(), params.toArray());
	}

	public Page<Content> paginateInNormal(int page, int pagesize, String module, BigInteger[] taxonomyIds,
			String orderBy) {

		LinkedList<Object> params = new LinkedList<Object>();

		String select = "select c.*";

		StringBuilder sql = new StringBuilder(" from content c");
		sql.append(" left join mapping m on c.id = m.`content_id`");
		sql.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");

		if (orderBy != null && orderBy.startsWith("meta:")) {
			sql.append(
					" left join metadata meta on meta.`object_type`='content' and meta.`object_id`=c.id and meta.`meta_key`=? ");
			params.add(orderBy.substring("meta:".length()));
		}

		sql.append(" WHERE c.status = 'normal' ");

		appendIfNotEmpty(sql, "c.module", module, params, false);

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			if (taxonomyIds.length == 1) {
				sql.append(" AND m.taxonomy_id = ?");
				params.add(taxonomyIds[0]);
			} else {
				sql.append(" AND exists(select 1 from mapping m where m.`taxonomy_id` in " + toString(taxonomyIds)
						+ " and m.`content_id`=c.id) ");
			}
		}

		sql.append(" group by c.id");

		if (orderBy != null && orderBy.startsWith("meta:")) {
			sql.append(" order by meta.`meta_value` + 0 desc ");
		} else {
			buildOrderBy(orderBy, sql);
		}

		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, sql.toString());
		}

		return DAO.paginate(page, pagesize, true, select, sql.toString(), params.toArray());
	}

	public Page<Content> paginate(int page, int pagesize, String module, String keyword, String status,
			BigInteger[] taxonomyIds, BigInteger userId, String orderBy) {

		String[] modules = StringUtils.isNotBlank(module) ? new String[] { module } : null;

		return paginate(page, pagesize, modules, keyword, status, taxonomyIds, userId, null, orderBy);
	}

	public Page<Content> paginate(int page, int pagesize, String[] modules, String keyword, String status,
			BigInteger[] taxonomyIds, BigInteger userId, String orderBy) {

		return paginate(page, pagesize, modules, keyword, status, taxonomyIds, userId, null, orderBy);
	}

	public Page<Content> paginate(int page, int pagesize, String[] modules, String keyword, String status,
			BigInteger[] taxonomyIds, BigInteger userId, String month, String orderBy) {

		String select = "select c.*";

		StringBuilder sql = new StringBuilder(" from content c");
		sql.append(" left join mapping m on c.id = m.`content_id`");
		sql.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sql, "c.module", modules, params, needWhere);
		needWhere = appendIfNotEmpty(sql, "c.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(sql, "c.user_id", userId, params, needWhere);

		if (StringUtils.isNotBlank(keyword)) {
			needWhere = appendWhereOrAnd(sql, needWhere);
			sql.append(" c.title like ? ");
			params.add("%" + keyword + "%");
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			needWhere = appendWhereOrAnd(sql, needWhere);
			sql.append(" t.id in " + toString(taxonomyIds));
		}

		if (StringUtils.isNotBlank(month)) {
			needWhere = appendWhereOrAnd(sql, needWhere);
			sql.append(" DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		sql.append(" group by c.id");

		buildOrderBy(orderBy, sql);

		if (params.isEmpty()) {
			return DAO.paginate(page, pagesize, true, select, sql.toString());
		}

		return DAO.paginate(page, pagesize, true, select, sql.toString(), params.toArray());
	}

	protected String toString(Object[] a) {
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

	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {

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

		else if ("price".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.price ");
		}

		else if ("comment_time".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.comment_time ");
		}

		else if ("rate".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.rate ");
		}

		else if ("rate_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.rate_count ");
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

	public List<Content> findListInNormal(int page, int pagesize, String orderBy, String keyword, BigInteger[] typeIds,
			String[] typeSlugs, String[] modules, String[] styles, String[] flags, String[] slugs, BigInteger[] userIds,
			BigInteger[] parentIds, String[] tags, Boolean hasThumbnail, String month) {

		if (modules == null) {
			modules = TemplateManager.me().currentTemplateModulesAsArray();
		}

		StringBuilder sql = new StringBuilder(" select  c.* from content c ");
		sql.append(" left join mapping m on c.id = m.`content_id`");
		sql.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");

		sql.append(" where c.status = 'normal' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(sql, "m.taxonomy_id", typeIds, params, false);
		appendIfNotEmpty(sql, "c.module", modules, params, false);
		appendIfNotEmpty(sql, "c.style", styles, params, false);
		appendIfNotEmpty(sql, "c.slug", slugs, params, false);
		appendIfNotEmpty(sql, "c.user_id", userIds, params, false);
		appendIfNotEmpty(sql, "c.parent_id", parentIds, params, false);
		appendIfNotEmpty(sql, "t.slug", typeSlugs, params, false);
		appendIfNotEmptyWithLike(sql, "c.flag", flags, params, false);

		if (null != tags && tags.length > 0) {
			appendIfNotEmpty(sql, "t.title", tags, params, false);
			sql.append(" AND t.`type`='tag' ");
		}

		if (StringUtils.isNotBlank(keyword)) {
			sql.append(" AND c.title like ?");
			params.add("%" + keyword + "%");
		}

		if (StringUtils.isNotBlank(month)) {
			sql.append(" AND DATE_FORMAT( c.created, \"%Y-%m\" ) = ?");
			params.add(month);
		}

		if (null != hasThumbnail) {
			if (hasThumbnail) {
				sql.append(" AND c.thumbnail is not null ");
			} else {
				sql.append(" AND c.thumbnail is null ");
			}
		}

		sql.append("GROUP BY c.id");

		buildOrderBy(orderBy, sql);

		sql.append(" LIMIT ?, ?");
		params.add(page - 1);
		params.add(pagesize);

		return DAO.find(sql.toString(), params.toArray());
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

	public Content findFirstByModuleAndObjectId(String module, BigInteger objectId, BigInteger userId) {
		return DAO.doFindFirst("module = ? and object_id = ? and user_id = ? order by id desc", module, objectId,
				userId);
	}

	public Content findFirstByModuleAndUserId(String module, BigInteger userId) {
		return DAO.doFindFirst("module = ? and user_id = ? order by id desc", module, userId);
	}

	public List<Content> findListByModuleAndObjectId(String module, BigInteger objectId) {
		return DAO.doFind("module = ? and object_id = ? order by id desc", module, objectId);
	}

	public List<Content> findListByModuleAndUserId(String module, BigInteger userId) {
		return DAO.doFind("module = ? and user_id = ? order by id desc", module, userId);
	}

	public List<Content> searchByModuleAndTitle(String module, String title, int limit) {
		return DAO.doFind("module = ? and title like ? order by id desc limit ?", module, "%" + title + "%", limit);
	}

	public List<Content> findByModule(final String module, final BigInteger parentId, String orderby) {
		final StringBuilder sqlBuilder = new StringBuilder("select * from content c");
		sqlBuilder.append(" where module = ? ");

		final List<Object> params = new ArrayList<Object>();
		params.add(module);
		appendIfNotEmpty(sqlBuilder, "parent_id", parentId, params, false);

		buildOrderBy(orderby, sqlBuilder);
		List<Content> data = DAO.getFromListCache(buildKey(module, parentId, orderby), new IDataLoader() {
			@Override
			public Object load() {
				return DAO.find(sqlBuilder.toString(), params.toArray());
			}
		});
		if (data == null)
			return null;

		return new ArrayList<Content>(data);
	}

	private String buildKey(String module, Object... params) {
		StringBuffer keyBuffer = new StringBuffer(module == null ? "" : "module:" + module);
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				keyBuffer.append("-p").append(i).append(":").append(params[i]);
			}
		}
		return keyBuffer.toString().replace(" ", "");
	}

	public List<Content> findArchiveByModule(String module) {
		StringBuilder sqlBuilder = new StringBuilder(
				" select  c.*,DATE_FORMAT( c.created, \"%Y-%m\" ) as archiveDate from content c ");
		sqlBuilder.append(" where module = ? ");
		sqlBuilder.append(" order by c.created DESC");
		return DAO.find(sqlBuilder.toString(), module);
	}

	public Content findBySlug(final String slug) {
		final StringBuilder sql = new StringBuilder(" select  c.* from content c ");
		sql.append(" WHERE c.slug = ?");
		return DAO.getCache(slug, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findFirst(sql.toString(), slug);
			}
		});
	}

	public Content findById(final BigInteger id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});

	}

	public Content findNext(final Content currentContent) {
		return DAO.getFromListCache(buildKey(currentContent.getModule(), "next:" + currentContent.getId()),
				new IDataLoader() {
					@Override
					public Object load() {
						StringBuilder sqlBuilder = new StringBuilder(" select ");
						sqlBuilder.append(" c.*,u.username,u.nickname,u.avatar ");
						sqlBuilder.append(" from content c");
						sqlBuilder.append(" left join user u on c.user_id = u.id ");
						sqlBuilder.append(" WHERE c.id > ?");
						sqlBuilder.append(" AND c.module = ?");
						sqlBuilder.append(" AND c.status = 'normal'");
						sqlBuilder.append(" ORDER BY c.created ASC");
						sqlBuilder.append(" LIMIT 1");
						return DAO.findFirst(sqlBuilder.toString(), currentContent.getId(), currentContent.getModule());
					}
				});
	}

	public Content findPrevious(final Content currentContent) {
		return DAO.getFromListCache(buildKey(currentContent.getModule(), "previous:" + currentContent.getId()),
				new IDataLoader() {
					@Override
					public Object load() {
						StringBuilder sqlBuilder = new StringBuilder(" select ");
						sqlBuilder.append(" c.*,u.username,u.nickname,u.avatar ");
						sqlBuilder.append(" from content c");
						sqlBuilder.append(" left join user u on c.user_id = u.id ");
						sqlBuilder.append(" WHERE c.id < ?");
						sqlBuilder.append(" AND c.module = ?");
						sqlBuilder.append(" AND c.status = 'normal'");
						sqlBuilder.append(" ORDER BY c.created DESC");
						sqlBuilder.append(" LIMIT 1");

						return DAO.findFirst(sqlBuilder.toString(), currentContent.getId(), currentContent.getModule());
					}
				});
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
			int trashCount = 0;
			for (int i = 0; i < ids.length; i++) {
				Content content = findById(ids[i]);
				if (content != null) {
					content.setStatus(Content.STATUS_DELETE);
					if (content.update()) {
						++trashCount;
					}

				}
			}
			return trashCount;
		}
		return 0;
	}

	public int batchDelete(BigInteger... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
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
