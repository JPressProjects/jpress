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
import io.jpress.model.ModelSorter.ISortModel;
import io.jpress.model.base.BaseTaxonomy;
import io.jpress.router.converter.TaxonomyRouter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

@Table(tableName = "taxonomy", primaryKey = "id")
public class Taxonomy extends BaseTaxonomy<Taxonomy> implements ISortModel<Taxonomy> {

	private static final long serialVersionUID = 1L;

	public static final Taxonomy DAO = new Taxonomy();

	public static final String TYPE_TAG = "tag"; // tag
	public static final String TYPE_SPECIAL = "special"; // 专题
	public static final String TYPE_CATEGORY = "category"; // 分类

	private int layer = 0;
	private List<Taxonomy> childList;
	private Taxonomy parent;

	public int getLayer() {
		return layer;
	}

	public void setLayer(int tier) {
		this.layer = tier;
	}

	public String getLayerString() {
		String layerString = "";
		for (int i = 0; i < layer; i++) {
			layerString += "— ";
		}
		return layerString;
	}

	public List<Taxonomy> getChildList() {
		return childList;
	}

	public void setChildList(List<Taxonomy> childList) {
		this.childList = childList;
	}

	public void addChild(Taxonomy child) {
		if (null == childList) {
			childList = new ArrayList<Taxonomy>();
		}
		childList.add(child);
	}

	public Taxonomy getParent() {
		return parent;
	}

	public void setParent(Taxonomy parent) {
		this.parent = parent;
	}

	public Page<Taxonomy> doPaginate(int page, int pagesize, String module) {
		return doPaginate(page, pagesize, "content_module = ?", module);
	}

	public List<Taxonomy> findListByModuleAndType(String module, String type) {
		return doFind("content_module = ? and type = ?", module, type);
	}

	public List<Taxonomy> findListByModuleAndType(String module, String type, int limit) {
		return doFind("content_module = ? and type = ? limit ?", module, type, limit);
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
		return DAO.doPaginate(pageNumber, pageSize, "content_module = ? and type = ? ", module, type);
	}

	public List<Taxonomy> findListByContentId(BigInteger contentId) {

		String sql = "select * from mapping m,taxonomy ";
		sql += "where  m.`taxonomy_id` = taxonomy.id ";
		sql += "and content_id = ? ";
		sql += "group by taxonomy.id ";

		return find(sql, contentId);
	}

	public List<Taxonomy> findListByTypeAndContentId(String type, BigInteger contentId) {

		String sql = "select * from mapping m,taxonomy ";
		sql += "where  m.`taxonomy_id` = taxonomy.id ";
		sql += "and `type`= ?  ";
		sql += "and content_id = ? ";
		sql += "group by taxonomy.id ";

		return find(sql, type, contentId);
	}

	public Taxonomy findBySlugAndModule(String slug, String module) {
		return doFindFirst("slug = ? and content_module=?", slug, module);
	}

	public List<Taxonomy> findListCategoryByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(TYPE_CATEGORY, contentId);
	}

	public List<Taxonomy> findListTagByContentId(BigInteger contentId) {
		return findListByTypeAndContentId(TYPE_TAG, contentId);
	}

	public long findContentCount() {
		Long count = Mapping.DAO.findCountByTaxonomyId(getId());
		return count == null ? 0 : count;
	}

	public String getUrl() {
		return TaxonomyRouter.getRouter(this);
	}

}
