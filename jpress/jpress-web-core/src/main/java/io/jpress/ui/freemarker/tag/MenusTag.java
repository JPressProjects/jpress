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
import io.jpress.model.vo.NavigationMenu;
import io.jpress.router.converter.TaxonomyRouter;
import io.jpress.utils.StringUtils;

public class MenusTag extends JTag {
	
	public static final String TAG_NAME = "jp.menus";

	private List<Taxonomy> currentTaxonomys;
	private Content currentContent;
	private HttpServletRequest request;

	public MenusTag(HttpServletRequest request) {
		this.request = request;
	}

	public MenusTag(HttpServletRequest request, List<Taxonomy> taxonomys, Content content) {
		this.request = request;
		this.currentTaxonomys = taxonomys;
		this.currentContent = content;
	}

	public MenusTag(HttpServletRequest request, Taxonomy taxonomy) {
		this.request = request;
		currentTaxonomys = new ArrayList<Taxonomy>();
		currentTaxonomys.add(taxonomy);
	}

	@Override
	public void onRender() {

		BigInteger parentId = getParamToBigInteger("parentId");
		String activeClass = getParam("activeClass","active");

		List<Content> list = null;

		if (parentId != null) {
			list = ContentQuery.me().findByModule(Consts.MODULE_MENU, parentId, "order_number ASC");
		} else {
			list = ContentQuery.me().findByModule(Consts.MODULE_MENU, "order_number ASC");
		}

		if (list == null || list.isEmpty()) {
			renderText("");
			return;
		}

		setActiveMenu(list);

		if (parentId == null) {
			ModelSorter.tree(list);
		}

		List<NavigationMenu> menulist = new ArrayList<NavigationMenu>();
		for (Content c : list) {
			menulist.add(new NavigationMenu(c, activeClass));
		}

		setVariable("menus", menulist);
		renderBody();
	}

	private void setActiveMenu(List<Content> menuContentList) {
		if (currentTaxonomys != null && currentTaxonomys.size() > 0) {
			for (Taxonomy taxonomy : currentTaxonomys) {
				String routerWithoutPageNumber = TaxonomyRouter.getRouterWithoutPageNumber(taxonomy);
				routerWithoutPageNumber = JFinal.me().getContextPath() + routerWithoutPageNumber;
				if (StringUtils.isNotBlank(routerWithoutPageNumber)) {
					for (Content menuContent : menuContentList) {
						if (menuContent.getText() != null
								&& menuContent.getText().startsWith(StringUtils.urlDecode(routerWithoutPageNumber))) {
							menuContent.put("active", "active");
						}
						
						String onlyModuleUrl = JFinal.me().getContextPath() + "/" + taxonomy.getContentModule();
						if (onlyModuleUrl.equals(menuContent.getText())) {
							menuContent.put("active", "active");
						}
					}
				}
			}
		}

		if (currentContent != null) {
			String contentUrl = currentContent.getUrl();
			for (Content menuContent : menuContentList) {
				if (contentUrl != null && contentUrl.equals(menuContent.getText())) {
					menuContent.put("active", "active");
				}

				String onlyModuleUrl = JFinal.me().getContextPath() + "/" + currentContent.getModule();
				if (onlyModuleUrl.equals(menuContent.getText())) {
					menuContent.put("active", "active");
				}
			}
		}

		for (Content menuContent : menuContentList) {
			if (menuContent.getText() != null
					&& menuContent.getText().equals(StringUtils.urlDecode(request.getRequestURI()))) {
				menuContent.put("active", "active");
			}
		}
	}

}
