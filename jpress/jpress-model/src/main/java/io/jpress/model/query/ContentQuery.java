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
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.core.db.Jdb;
import io.jpress.model.Content;

public class ContentQuery extends JBaseQuery {

	private static final Content MODEL = new Content();

	public static boolean deleteById(BigInteger id) {
		return MODEL.deleteById(id);
	}

	public static Page<Content> paginateByMetadata(int page, int pagesize, String meta_key, String meta_value) {
		return MODEL.paginate(page, pagesize, true, "select * ",
				"FROM (select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,"
						+ "GROUP_CONCAT(m.id ,':',m.meta_key,':',m.meta_value SEPARATOR ',') metadatas , u.username"
						+ " FROM content c" + " left join mapping m on c.id = m.`content_id`"
						+ " left join taxonomy  t on m.`taxonomy_id` = t.id" + " left join user u on c.user_id = u.id"
						+ " left join metadata md on c.id = md.`object_id` and md.`object_type`='content'"
						+ " where c.`metadatas` like ?" + " GROUP BY c.id" + " ORDER BY c.created DESC) c ",
				"%:" + meta_key + ":" + meta_value);
	}

	public static Page<Content> paginateByModule(int page, int pagesize, String module) {
		return paginate(page, pagesize, module, null, null, null, null);
	}

	public static Page<Content> paginateByModuleAndStatus(int page, int pagesize, String module, String status,
			String orderBy) {
		return paginate(page, pagesize, module, status, null, null, orderBy);
	}

	public static Page<Content> paginateByModuleAndStatus(int page, int pagesize, String module, String status) {
		return paginate(page, pagesize, module, status, null, null, null);
	}

	public static Page<Content> paginateByModuleInNormal(int page, int pagesize, String module) {
		return paginate(page, pagesize, module, Content.STATUS_NORMAL, null, null, null);
	}

	public static Page<Content> paginateByModuleNotInDelete(int page, int pagesize, String module) {
		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username";

		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");
		fromBuilder.append(" where c.status <> ?");

		LinkedList<Object> params = new LinkedList<Object>();
		params.add(Content.STATUS_DELETE);

		boolean needWhere = false;
		needWhere = appendIfNotEmpty(fromBuilder, "c.module", module, params, needWhere);
		fromBuilder.append(" group by c.id");
		fromBuilder.append(" ORDER BY c.created DESC");

		if (params.isEmpty()) {
			return MODEL.paginate(page, pagesize, select, fromBuilder.toString());
		}

		return MODEL.paginate(page, pagesize, true, select, fromBuilder.toString(), params.toArray());
	}

	public static Page<Content> paginate(int page, int pagesize, String module, String status, BigInteger taxonomyId,
			BigInteger userId, String orderBy) {

		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username";

		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "c.module", module, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "t.id", taxonomyId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "u.id", userId, params, needWhere);

		fromBuilder.append(" group by c.id");

		buildOrderBy(orderBy, fromBuilder);

		if (params.isEmpty()) {
			return MODEL.paginate(page, pagesize, select, fromBuilder.toString());
		}

		return MODEL.paginate(page, pagesize, true, select, fromBuilder.toString(), params.toArray());
	}

	private static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		if("view_count".equals(orderBy)){
			fromBuilder.append(" ORDER BY c.view_count DESC");
		}
		
		else if ("comment_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.comment_count DESC");
		} 
		
		else if ("modified".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.modified DESC");
		} 
		
		else if ("vote_up".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.vote_up DESC");
		} 
		
		else if ("vote_down".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.vote_down DESC");
		} 
		
		else if ("order_number".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.order_number DESC");
		} 
		
		else if ("parent_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.parent_id DESC");
		} 
		
		else if ("object_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY c.object_id DESC");
		} 
		else {
			fromBuilder.append(" ORDER BY c.created DESC");
		}
	}

	public static Long findCountByModuleAndStatus(String module, String status) {
		return MODEL.doFindCount("module = ? and status=?", module, status);
	}

	public static List<Content> findListInNormal(int page, int pagesize, BigInteger taxonomyId, String orderBy) {
		return findListInNormal(page, pagesize, orderBy, null, new BigInteger[] { taxonomyId }, null, null, null, null,
				null, null, null, null, null);
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
	public static List<Content> findListInNormal(int page, int pagesize, String orderBy, String keyword,
			BigInteger[] typeIds, String[] typeSlugs, String[] modules, String[] styles, String[] flags, String[] slugs,
			BigInteger[] userIds, BigInteger[] parentIds, String[] tags, Boolean hasThumbnail) {

		StringBuilder sqlBuilder = getBaseSelectSql();
		sqlBuilder.append(" where c.status = 'normal' ");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = false;
		needWhere = appendIfNotEmpty(sqlBuilder, "m.taxonomy_id", typeIds, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.module", modules, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.style", styles, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.slug", slugs, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.user_id", userIds, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.parent_id", parentIds, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "t.slug", typeSlugs, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(sqlBuilder, "c.flag", flags, params, needWhere);

		if (null != tags && tags.length > 0) {
			needWhere = appendIfNotEmpty(sqlBuilder, "t.name", tags, params, needWhere);
			sqlBuilder.append(" AND t.taxonomy_module='tag' ");
		}

		if (null != keyword && !"".equals(keyword.trim())) {
			needWhere = appendWhereOrAnd(sqlBuilder, needWhere);
			sqlBuilder.append(" c.title like ?");
			params.add("%" + keyword + "%");
		}

		if (null != hasThumbnail) {
			if (hasThumbnail) {
				sqlBuilder.append(" and c.thumbnail is not null ");
			} else {
				sqlBuilder.append(" and c.thumbnail is null ");
			}
		}

		sqlBuilder.append("GROUP BY c.id");

		buildOrderBy(orderBy, sqlBuilder);

		sqlBuilder.append(" LIMIT ?, ?");
		params.add(page - 1);
		params.add(pagesize);

		return MODEL.find(sqlBuilder.toString(), params.toArray());
	}

	public static List<Content> findByModule(String module) {
		return MODEL.doFind("module = ? ", module);
	}

	public static List<Content> findByModuleAndTitle(String module, String title, int limit) {
		return MODEL.doFind("module = ? and title = ? order by id desc limit ?", module, title, limit);
	}

	public static Content findFirstByModuleAndTitle(String module, String title) {
		return MODEL.doFindFirst("module = ? and title = ? order by id desc", module, title);
	}

	public static Content findFirstByModuleAndText(String module, String text) {
		return MODEL.doFindFirst("module = ? and text = ? order by id desc", module, text);
	}

	public static Content findFirstByModuleAndObjectId(String module, BigInteger objectId) {
		return MODEL.doFindFirst("module = ? and object_id = ? order by id desc", module, objectId);
	}

	public static List<Content> searchByModuleAndTitle(String module, String title, int limit) {
		return MODEL.doFind("module = ? and title like ? order by id desc limit ?", module, "%" + title + "%", limit);
	}

	public static List<Content> findByModule(String module, String orderby) {
		return MODEL.doFind("module = ? order by ?", module, orderby);
	}

	public static List<Content> findByModule(String module, BigInteger parentId, String orderby) {
		return MODEL.doFind("module = ? AND parent_id = ? order by ?", module, parentId, orderby);
	}

	public static Content findBySlug(final String slug) {
		final StringBuilder sql = getBaseSelectSql();

		sql.append(" WHERE c.slug = ?");
		sql.append(" GROUP BY c.id");

		return MODEL.getCache(slug, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.findFirst(sql.toString(), slug);
			}
		});
	}

	public static Content findById(final BigInteger id) {
		final StringBuilder sql = getBaseSelectSql();
		sql.append(" WHERE c.id = ?");
		sql.append(" GROUP BY c.id");

		return MODEL.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.findFirst(sql.toString(), id);
			}
		});
	}

	private static StringBuilder getBaseSelectSql() {
		StringBuilder sqlBuilder = new StringBuilder(
				"select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username,u.nickname,u.avatar");
		sqlBuilder.append(" from content c");
		sqlBuilder.append(" left join mapping m on c.id = m.`content_id`");
		sqlBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		sqlBuilder.append(" left join user u on c.user_id = u.id");
		return sqlBuilder;
	}

	public static Content findById(Object idValue) {
		return MODEL.findFirst(getBaseSelectSql() + " WHERE c.id=? ", idValue);
	}

	public static long findCountByModule(String module) {
		return MODEL.doFindCount("module = ?", module);
	}

	public static long findCountInNormalByModule(String module) {
		return MODEL.doFindCount("module = ? AND status <> ?", module, Content.STATUS_DELETE);
	}

	public static long findCountInNormalByModuleAndUserId(String module, BigInteger userId) {
		return MODEL.doFindCount("module = ? AND status <> ? and user_id = ? ", module, Content.STATUS_DELETE, userId);
	}

	public static long findCountInNormalByParentId(BigInteger id, String module) {
		if (id == null) {
			return MODEL.doFindCount("parent_id is null AND module = ? AND status <> ?", module, Content.STATUS_DELETE);
		}
		return MODEL.doFindCount("parent_id = ? AND module = ? AND status <> ?", id, module, Content.STATUS_DELETE);
	}

	public static int batchTrash(BigInteger... ids) {
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

	public static int batchDelete(BigInteger... ids) {
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

}
