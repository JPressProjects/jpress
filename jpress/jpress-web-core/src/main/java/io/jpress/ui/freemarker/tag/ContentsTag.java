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
package io.jpress.ui.freemarker.tag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.utils.StringUtils;

public class ContentsTag extends JTag {

	public static final String TAG_NAME = "jp.contents";

	@Override
	public void onRender() {

		String orderBy = getParam("orderBy");
		String keyword = getParam("keyword");

		int pageNumber = getParamToInt("page", 1);
		int pageSize = getParamToInt("pageSize", 10);

		Integer count = getParamToInt("count");
		if (count != null && count > 0) {
			pageSize = count;
		}

		BigInteger[] typeIds = getParamToBigIntegerArray("typeId");
		String[] typeSlugs = getParamToStringArray("typeSlug");
		String[] tags = getParamToStringArray("tag");
		String[] modules = getParamToStringArray("module");
		String[] styles = getParamToStringArray("style");
		String[] flags = getParamToStringArray("flag");
		String[] slugs = getParamToStringArray("slug");
		BigInteger[] userIds = getParamToBigIntegerArray("userId");
		BigInteger[] parentIds = getParamToBigIntegerArray("parentId");
		Boolean hasThumbnail = getParamToBool("hasThumbnail");

		Taxonomy upperTaxonomy = null;
		if (modules != null && modules.length == 1) {
			
			BigInteger upperId = getParamToBigInteger("upperId");
			
			if (upperId != null) {
				upperTaxonomy = TaxonomyQuery.me().findById(upperId);
			}

			if (upperTaxonomy == null) {
				String upperSlug = getParam("upperSlug");
				if (StringUtils.isNotBlank(upperSlug)) {
					upperTaxonomy = TaxonomyQuery.me().findBySlugAndModule(upperSlug, modules[0]);
				}
			}
		}

		if (upperTaxonomy != null) {
			List<Taxonomy> list = TaxonomyQuery.me().findListByModuleAndType(modules[0], null);
			// 找到taxonomy id的所有孩子或孙子
			List<Taxonomy> newlist = new ArrayList<Taxonomy>();
			ModelSorter.sort(list, newlist, upperTaxonomy.getId(), 0);
			if (newlist != null && newlist.size() > 0) {
				slugs = null; // 设置 slugs无效
				typeIds = new BigInteger[newlist.size() + 1];
				typeIds[0] = upperTaxonomy.getId();
				for (int i = 1; i < typeIds.length; i++) {
					typeIds[i] = newlist.get(i - 1).getId();
				}
			}
		}

		List<Content> data = ContentQuery.me().findListInNormal(pageNumber, pageSize, orderBy, keyword, typeIds,
				typeSlugs, modules, styles, flags, slugs, userIds, parentIds, tags, hasThumbnail, null);

		if (data == null || data.isEmpty()) {
			renderText("");
			return;
		}

		setVariable("contents", data);
		renderBody();
	}

}
