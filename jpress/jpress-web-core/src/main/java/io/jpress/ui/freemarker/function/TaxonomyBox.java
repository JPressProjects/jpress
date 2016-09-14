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
package io.jpress.ui.freemarker.function;

import java.util.List;

import io.jpress.core.render.freemarker.JFunction;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.template.TplTaxonomyType;

public class TaxonomyBox extends JFunction {

	private TplTaxonomyType taxonomyType;
	private Content content;
	private List<Taxonomy> contentTaxonomyList;

	@Override
	public Object onExec() {
		init();
		return doExec();
	}

	private void init() {

		this.taxonomyType = (TplTaxonomyType) get(0);
		this.content = (Content) get(1);

		if (content != null) {
			contentTaxonomyList = TaxonomyQuery.me().findListByContentId(content.getId());
		} else {
			contentTaxonomyList = null;
		}
	}

	private Object doExec() {

		String moduleName = taxonomyType.getModule().getName();
		String txType = taxonomyType.getName();
		List<Taxonomy> list = TaxonomyQuery.me().findListByModuleAndTypeAsTree(moduleName, txType);
		StringBuilder htmlBuilder = new StringBuilder();
		if (list != null && list.size() > 0) {
			doBuilder(list, htmlBuilder);
		}
		return htmlBuilder.toString();
	}

	private void doBuilder(List<Taxonomy> list, StringBuilder htmlBuilder) {
		htmlBuilder.append("<ul>");
		for (Taxonomy taxonomy : list) {
			
			boolean checked = contentTaxonomyList != null && contentTaxonomyList.contains(taxonomy);
			String html = "<li ><label><input  name=\"_%s\" value=\"%s\" %s type=\"checkbox\"/>%s</label></li>";
			htmlBuilder.append(String.format(html, taxonomyType.getName(), taxonomy.getId(),
					checked ? "checked=\"checked\"" : "", taxonomy.getTitle()));

			if (taxonomy.getChildList() != null && taxonomy.getChildList().size() > 0) {
				doBuilder(taxonomy.getChildList(), htmlBuilder);
			}
		}
		htmlBuilder.append("</ul>");
	}

}
