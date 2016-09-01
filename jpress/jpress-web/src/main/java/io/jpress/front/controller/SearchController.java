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
package io.jpress.front.controller;

import java.io.UnsupportedEncodingException;

import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.core.cache.ActionCache;
import io.jpress.router.RouterMapping;
import io.jpress.template.TemplateManager;
import io.jpress.ui.freemarker.tag.SearchResultPageTag;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/s")
public class SearchController extends BaseFrontController {

	@ActionCache
	public void index() throws UnsupportedEncodingException {
		keepPara();

		String keyword = getPara("k");
		if (StringUtils.isBlank(keyword)) {
			renderError(404);
		}

		String moduleName = getPara("m", Consts.MODULE_ARTICLE);
		if (TemplateManager.me().currentTemplateModule(moduleName) == null) {
			renderError(404);
		}

		int pageNumber = getParaToInt("p", 1);
		pageNumber = pageNumber < 1 ? 1 : pageNumber;

		setAttr("keyword", StringUtils.escapeHtml(keyword));
		setAttr(SearchResultPageTag.TAG_NAME, new SearchResultPageTag(keyword, moduleName, pageNumber));
		render(String.format("search_%s.html", moduleName));
	}

}
