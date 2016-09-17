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
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.utils.StringUtils;

public class TaxonomyQuery extends JBaseQuery {

	protected static final Taxonomy DAO = new Taxonomy();
	private static final TaxonomyQuery QUERY = new TaxonomyQuery();

	public static TaxonomyQuery me() {
		return QUERY;
	}

	public Taxonomy findById(final BigInteger id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public List<Taxonomy> findAll() {
		return DAO.doFind();
	}

	public List<Taxonomy> findListCategoryByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(Taxonomy.TYPE_CATEGORY, contentId);
	}

	public List<Taxonomy> findListTagByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(Taxonomy.TYPE_TAG, contentId);
	}

	public Page<Taxonomy> doPaginate(int page, int pagesize, String module) {
		return DAO.doPaginate(page, pagesize, "content_module = ?", module);
	}

	public List<Taxonomy> findListByModuleAndType(String module, String type) {
		return findListByModuleAndType(module, type, null, null, null);
	}

	public List<Taxonomy> findListByModuleAndType(String module, String type, BigInteger parentId, Integer limit,
			String orderby) {

		final StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM taxonomy t");

		boolean needWhere = true;
		final List<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(sqlBuilder, "t.content_module", module, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "t.`type`", type, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "t.`parent_id`", parentId, params, needWhere);

		buildOrderBy(orderby, sqlBuilder);

		if (limit != null) {
			sqlBuilder.append(" limit ? ");
			params.add(limit);
		}

		String key = buildKey(module, type, parentId, limit, orderby);
		List<Taxonomy> data = DAO.getFromListCache(key, new IDataLoader() {
			@Override
			public Object load() {
				if (params.isEmpty()) {
					return DAO.find(sqlBuilder.toString());
				}
				return DAO.find(sqlBuilder.toString(), params.toArray());
			}
		});
		
		if (data == null)
			return null;
		return new ArrayList<Taxonomy>(data);
	}

	public List<Taxonomy> findListByModuleAndTypeAsTree(String module, String type) {
		List<Taxonomy> list = findListByModuleAndType(module, type);
		ModelSorter.tree(list);
		return list;
	}

	public List<Taxonomy> findListByModuleAndTypeAsSort(String module, String type) {
		List<Taxonomy> list = findListByModuleAndType(module, type);
		ModelSorter.sort(list);
		return list;
	}

	public Page<Taxonomy> doPaginate(int pageNumber, int pageSize, String module, String type) {
		return DAO.doPaginate(pageNumber, pageSize, "content_module = ? and type = ? order by created desc", module,
				type);
	}

	public List<Taxonomy> findListByContentId(BigInteger contentId) {

		StringBuilder sqlBuilder = new StringBuilder("select t.* from taxonomy t");
		sqlBuilder.append(" left join mapping m on t.id = m.taxonomy_id ");
		sqlBuilder.append(" where m.content_id = ? ");

		return DAO.find(sqlBuilder.toString(), contentId);
	}

	public List<Taxonomy> findListByTypeAndContentId(String type, BigInteger contentId) {

		StringBuilder sqlBuilder = new StringBuilder("select t.* from taxonomy t");
		sqlBuilder.append(" left join mapping m on t.id = m.taxonomy_id ");
		sqlBuilder.append(" where m.content_id = ? ");
		sqlBuilder.append(" and t.`type` = ? ");

		return DAO.find(sqlBuilder.toString(), type, contentId);
	}

	public Taxonomy findBySlugAndModule(String slug, String module) {
		return DAO.doFindFirstByCache(Taxonomy.CACHE_NAME, module + ":" + slug, "slug = ? and content_module=?", slug,
				module);
	}

	public List<Taxonomy> findBySlugAndModule(String[] slugs, String module) {

		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM taxonomy t");

		boolean needWhere = true;
		List<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(sqlBuilder, "t.content_module", module, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "t.`slug`", slugs, params, needWhere);

		return DAO.find(sqlBuilder.toString(), params.toArray());

	}

	public boolean deleteById(BigInteger id) {
		return DAO.deleteById(id);
	}

	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {

		if (StringUtils.isBlank(orderBy)) {
			fromBuilder.append(" ORDER BY t.created DESC");
			return;
		}

		// maybe orderby == "view_count desc";
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];

		if ("title".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.title ");
		}

		else if ("slug".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.slug ");
		}

		else if ("content_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.content_count ");
		}

		else if ("order_number".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.order_number ");
		}

		else if ("parent_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.parent_id ");
		}

		else if ("object_id".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.object_id ");
		}

		else if ("text".equals(orderBy)) {
			fromBuilder.append(" ORDER BY t.text ");
		}

		else {
			fromBuilder.append(" ORDER BY t.created ");
		}

		if (orderbyInfo.length == 1) {
			fromBuilder.append(" DESC ");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}
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

}
