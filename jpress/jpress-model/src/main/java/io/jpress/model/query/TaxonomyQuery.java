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
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;

public class TaxonomyQuery extends JBaseQuery {

	private static Taxonomy MODEL = new Taxonomy();
	
	public static Taxonomy findById(BigInteger id){
		return MODEL.findById(id);
	}
	
	public static List<Taxonomy> findAll(){
		return MODEL.doFind();
	}

	public static List<Taxonomy> findListCategoryByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(Taxonomy.TYPE_CATEGORY, contentId);
	}

	public static List<Taxonomy> findListTagByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(Taxonomy.TYPE_TAG, contentId);
	}

	public static Page<Taxonomy> doPaginate(int page, int pagesize, String module) {
		return MODEL.doPaginate(page, pagesize, "content_module = ?", module);
	}

	public static List<Taxonomy> findListByModuleAndType(String module, String type) {
		return MODEL.doFind("content_module = ? and type = ?", module, type);
	}

	public static List<Taxonomy> findListByModuleAndType(String module, String type, int limit) {
		return MODEL.doFind("content_module = ? and type = ? limit ?", module, type, limit);
	}

	public static List<Taxonomy> findListByModuleAndTypeAsTree(String module, String type) {
		List<Taxonomy> list = findListByModuleAndType(module, type);
		ModelSorter.tree(list);
		return list;
	}

	public static List<Taxonomy> findListByModuleAndTypeAsSort(String module, String type) {
		List<Taxonomy> list = findListByModuleAndType(module, type);
		ModelSorter.sort(list);
		return list;
	}

	public static Page<Taxonomy> doPaginate(int pageNumber, int pageSize, String module, String type) {
		return MODEL.doPaginate(pageNumber, pageSize, "content_module = ? and type = ? order by created desc", module, type);
	}

	public static List<Taxonomy> findListByContentId(BigInteger contentId) {

		StringBuilder sqlBuilder = new StringBuilder("select * from taxonomy t");
		sqlBuilder.append(" left join mapping m on t.id = m.taxonomy_id ");
		sqlBuilder.append(" where m.content_id = ? ");

		return MODEL.find(sqlBuilder.toString(), contentId);
	}

	public static List<Taxonomy> findListByTypeAndContentId(String type, BigInteger contentId) {

		StringBuilder sqlBuilder = new StringBuilder("select * from taxonomy t");
		sqlBuilder.append(" left join mapping m on t.id = m.taxonomy_id ");
		sqlBuilder.append(" where m.content_id = ? ");
		sqlBuilder.append(" and t.`type` = ? ");

		return MODEL.find(sqlBuilder.toString(), type, contentId);
	}

	public static Taxonomy findBySlugAndModule(String slug, String module) {
		return MODEL.doFindFirst("slug = ? and content_module=?", slug, module);
	}
	
	public static boolean deleteById(BigInteger id){
		return MODEL.deleteById(id);
	}

}
