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

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.JFinal;

import io.jpress.Consts;
import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.router.converter.TaxonomyRouter;
import io.jpress.utils.StringUtils;

public class MenuTag extends JTag {

	private List<Taxonomy> currentTaxonomys;
	private HttpServletRequest request;

	public MenuTag(HttpServletRequest request) {
		this.request = request;
	}

	public MenuTag(HttpServletRequest request, List<Taxonomy> taxonomys) {
		this.request = request;
		currentTaxonomys = taxonomys;
	}

	public MenuTag(HttpServletRequest request, Taxonomy taxonomy) {
		this.request = request;
		currentTaxonomys = new ArrayList<Taxonomy>();
		currentTaxonomys.add(taxonomy);
	}

	@Override
	public void onRender() {

		BigInteger parentId = getParamToBigInteger("parentId");
		List<Content> list = null;
		if (parentId != null) {
			list = ContentQuery.findByModule(Consts.MODULE_MENU, parentId, "order_number ASC");
		} else {
			list = ContentQuery.findByModule(Consts.MODULE_MENU, "order_number ASC");
		}

		if (list == null || list.isEmpty()) {
			renderText("");
			return;
		}

		if (getParamToBool("withContextPath", true)) {
			processContextPath(list);
		}

		setActiveMenu(list);
		ModelSorter.tree(list);
		setVariable("menus", list);
		renderBody();
	}

	private void processContextPath(List<Content> list) {
		for (Content c : list) {
			String url = c.getText();
			if (!StringUtils.isNotBlank(url)) {
				continue;
			}

			if (!url.toLowerCase().startsWith("http:") && !url.toLowerCase().startsWith("https:")) {
				url = JFinal.me().getContextPath() + url;
			}
			
			c.setText(url);
		}
	}

	private void setActiveMenu(List<Content> list) {
		if (currentTaxonomys != null && currentTaxonomys.size() > 0) {
			for (Taxonomy taxonomy : currentTaxonomys) {
				String routerWithoutPageNumber = TaxonomyRouter.getRouterWithoutPageNumber(taxonomy);
				if (StringUtils.isNotBlank(routerWithoutPageNumber)) {
					for (Content content : list) {
						if (content.getText() != null && content.getText().startsWith(routerWithoutPageNumber)) {
							content.setFlag("active");
						}
					}
				}
			}
		}

		for (Content c : list) {
			if (c.getText() != null && c.getText().equals(request.getRequestURI())) {
				c.setFlag("active");
			}
		}
	}

}
