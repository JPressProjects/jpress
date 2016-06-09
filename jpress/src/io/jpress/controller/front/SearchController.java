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
package io.jpress.controller.front;

import java.util.List;

import io.jpress.core.cache.ActionCache;
import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.plugin.search.SearcherFactory;
import io.jpress.router.RouterMapping;

@RouterMapping(url = "/s")
public class SearchController extends BaseFrontController {

	@ActionCache
	public void index() {
		String keyword = getPara("k");
		int pageNumber = getParaToInt("n");
		int pageSize = getParaToInt("s");
//		String module = getPara("m");

		ISearcher searcher = SearcherFactory.createSearcher();
		if (searcher != null) {
			List<SearcherBean> results = searcher.search(keyword, pageNumber, pageSize);
			setAttr("result", results);
		}

		render("search.html");
	}

}
