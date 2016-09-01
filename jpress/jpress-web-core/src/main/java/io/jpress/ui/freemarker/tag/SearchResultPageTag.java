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

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.render.freemarker.BasePaginateTag;
import io.jpress.core.render.freemarker.JTag;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.plugin.search.SearcherKit;
import io.jpress.utils.StringUtils;

public class SearchResultPageTag extends JTag {
	
	public static final String TAG_NAME = "jp.searchResultPage";
	
	int pageNumber;
	String moduleName;
	String keyword;

	public SearchResultPageTag(String keyword, String moduleName, int pageNumber) {
		this.pageNumber = pageNumber;
		this.moduleName = moduleName;
		this.keyword = keyword;
	}

	@Override
	public void onRender() {

		int pagesize = getParamToInt("pageSize", 10);

		Page<SearcherBean> page = SearcherKit.search(keyword, moduleName, pageNumber, pagesize);
		setVariable("page", page);
		setVariable("searcherBeans", page.getList());

		MyPaginateTag pagination = new MyPaginateTag(page, keyword, moduleName);
		setVariable("pagination", pagination);

		renderBody();
	}

	public static class MyPaginateTag extends BasePaginateTag {

		final String keyword;
		final String moduleName;

		public MyPaginateTag(Page<SearcherBean> page, String keyword, String moduleName) {
			super(page);
			this.keyword = keyword;
			this.moduleName = moduleName;
		}

		@Override
		protected String getUrl(int pageNumber) {
			String url = JFinal.me().getContextPath() + "/s?";
			url += "m=" + (StringUtils.isNotBlank(moduleName) ? moduleName.trim() : "");
			url += "&k=" + StringUtils.urlDecode(keyword);
			url += "&p=" + pageNumber;

			if (StringUtils.isNotBlank(getAnchor())) {
				url += "#" + getAnchor();
			}
			return url;
		}

	}

}
