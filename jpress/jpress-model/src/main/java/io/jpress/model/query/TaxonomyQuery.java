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

import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;

public class TaxonomyQuery extends JBaseQuery {

	private static final Taxonomy DAO = new Taxonomy();
	private static final TaxonomyQuery QUERY = new TaxonomyQuery();

	public static TaxonomyQuery me() {
		return QUERY;
	}

	public Taxonomy findById(BigInteger id) {
		return DAO.findById(id);
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
		return findListByModuleAndType(module, type, null);
	}

	public List<Taxonomy> findListByModuleAndType(String module, String type, Integer limit) {

		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM taxonomy ");

		boolean needWhere = true;
		List<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(sqlBuilder, "content_module", module, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "type", type, params, needWhere);

		if (limit != null) {
			sqlBuilder.append(" limit ? ");
			params.add(limit);
		}

		return DAO.find(sqlBuilder.toString(), params.toArray());
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
		return DAO.doFindFirst("slug = ? and content_module=?", slug, module);
	}

	public boolean deleteById(BigInteger id) {
		return DAO.deleteById(id);
	}

}
