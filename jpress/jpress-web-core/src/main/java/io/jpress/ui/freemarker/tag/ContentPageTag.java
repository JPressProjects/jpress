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
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.render.freemarker.BasePaginateTag;
import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.utils.StringUtils;

public class ContentPageTag extends JTag {

	public static final String TAG_NAME = "jp.contentPage";

	int pageNumber;
	String moduleName;
	String orderBy;
	List<Taxonomy> taxonomys;
	HttpServletRequest request;

	public ContentPageTag(HttpServletRequest request, int pageNumber, String moduleName, List<Taxonomy> taxonomys,
			String orderBy) {
		this.request = request;
		this.pageNumber = pageNumber;
		this.moduleName = moduleName;
		this.taxonomys = taxonomys;
		this.orderBy = orderBy;
	}

	@Override
	public void onRender() {

		int pagesize = getParamToInt("pageSize", 10);
		orderBy = StringUtils.isBlank(orderBy) ? getParam("orderBy") : orderBy;

		BigInteger[] taxonomyIds = null;
		if (taxonomys != null && taxonomys.size() > 0) {
			taxonomyIds = new BigInteger[taxonomys.size()];
			for (int i = 0; i < taxonomyIds.length; i++) {
				taxonomyIds[i] = taxonomys.get(i).getId();
			}
		}

		if (taxonomyIds != null && taxonomyIds.length > 0) {
			boolean containChild = getParamToBool("containChild", false);
			if (containChild == true) {
				for (Taxonomy taxonomy : taxonomys) {
					List<Taxonomy> childs = TaxonomyQuery.me().findListByModuleAndType(moduleName, taxonomy.getType());
					if (childs != null && childs.size() > 0) {
						ModelSorter.sort(childs, taxonomy.getId());
						BigInteger[] newIds = Arrays.copyOf(taxonomyIds, taxonomyIds.length + childs.size());
						for (int i = taxonomyIds.length; i < newIds.length; i++) {
							newIds[i] = childs.get(i - taxonomyIds.length).getId();
						}
						taxonomyIds = newIds;
					}
				}
			}
		}

		Page<Content> page = ContentQuery.me().paginateInNormal(pageNumber, pagesize, moduleName, taxonomyIds, orderBy);
		setVariable("page", page);
		setVariable("contents", page.getList());

		ContentPaginateTag pagination = new ContentPaginateTag(request, page, moduleName, taxonomys);
		setVariable("pagination", pagination);

		renderBody();
	}

	public static class ContentPaginateTag extends BasePaginateTag {

		final String moduleName;
		final List<Taxonomy> taxonomys;
		final HttpServletRequest request;

		public ContentPaginateTag(HttpServletRequest request, Page<Content> page, String moduleName,
				List<Taxonomy> taxonomys) {
			super(page);
			this.request = request;
			this.moduleName = moduleName;
			this.taxonomys = taxonomys;

		}

		@Override
		protected String getUrl(int pageNumber) {
			String url = JFinal.me().getContextPath() + "/" + moduleName + "-";
			if (taxonomys != null && taxonomys.size() > 0) {
				for (Taxonomy taxonomy : taxonomys) {
					url += taxonomy.getSlug() + ",";
				}
				url = url.substring(0, url.length() - 1);
				url += "-" + pageNumber;
			} else {
				url += pageNumber;
			}

			if (enalbleFakeStatic()) {
				url += getFakeStaticSuffix();
			}

			String queryString = request.getQueryString();
			if (StringUtils.isNotBlank(queryString)) {
				url += "?" + queryString;
			}

			if (StringUtils.isNotBlank(getAnchor())) {
				url += "#" + getAnchor();
			}
			return url;
		}

	}

}
