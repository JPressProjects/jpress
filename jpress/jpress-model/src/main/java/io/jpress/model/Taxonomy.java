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

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.JFinal;

import io.jpress.model.ModelSorter.ISortModel;
import io.jpress.model.base.BaseTaxonomy;
import io.jpress.model.core.Table;
import io.jpress.model.query.MappingQuery;
import io.jpress.model.query.MetaDataQuery;
import io.jpress.model.utils.TaxonomyRouter;
import io.jpress.utils.StringUtils;

@Table(tableName = "taxonomy", primaryKey = "id")
public class Taxonomy extends BaseTaxonomy<Taxonomy> implements ISortModel<Taxonomy> {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_TAG = "tag"; // tag
	public static final String TYPE_SPECIAL = "special"; // 专题
	public static final String TYPE_CATEGORY = "category"; // 分类

	private int layer = 0;
	private List<Taxonomy> childList;
	private Taxonomy parent;

	public int getLayer() {
		return layer;
	}

	@Override
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

	@Override
	public void addChild(Taxonomy child) {
		if (null == childList) {
			childList = new ArrayList<Taxonomy>();
		}
		childList.add(child);
	}

	@Override
	public Taxonomy getParent() {
		return parent;
	}

	@Override
	public void setParent(Taxonomy parent) {
		this.parent = parent;
	}

	public void updateContentCount() {
		long count = MappingQuery.me().findCountByTaxonomyId(getId(), Content.STATUS_NORMAL);
		if (count > 0) {
			setContentCount(count);
			this.update();
		}
	}

	public long findContentCount() {
		Long count = MappingQuery.me().findCountByTaxonomyId(getId());
		return count == null ? 0 : count;
	}

	public String getUrl() {
		return JFinal.me().getContextPath() + TaxonomyRouter.getRouter(this);
	}

	@Override
	public boolean saveOrUpdate() {
		if (getId() != null) {
			removeCache(getId());
			putCache(getId(), this);
		}

		return super.saveOrUpdate();
	}

	public boolean update() {
		if (getId() != null) {
			removeCache(getId());
			putCache(getId(), this);
		}
		return super.update();
	}

	public String metadata(String key) {
		Metadata m = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);
		if (m != null) {
			return m.getMetaValue();
		}
		return null;
	}
	
	@Override
	public void setSlug(String slug) {
		if (StringUtils.isNotBlank(slug)) {
			slug = slug.trim();
			if (StringUtils.isNumeric(slug)) {
				slug = "c" + slug; // slug不能为全是数字,随便添加一个字母，c代表content好了
			} else {
				slug = slug.replaceAll("(?!_)\\pP|\\pS|(\\s+)|(\\.+)|(。+)|(…+)|[\\$,，？\\-?、；;:!]", "");
			}
		}
		super.setSlug(slug);
	}

}
