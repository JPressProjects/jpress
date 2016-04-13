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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import io.jpress.Consts;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Taxonomy;
import io.jpress.template.Module;
import io.jpress.utils.StringUtils;

@UrlMapping(url = Consts.TAXONOMY_BASE_URL)
public class TaxonomyController extends BaseFrontController {

	// http://www.xxx.com/t/module-slug-pageNumber.html
	// http://www.xxx.com/t/module-id-pageNumber.html
	// http://www.xxx.com/t/article-1-1.html
	// http://www.xxx.com/t/article-slug1-1.html

	// http://www.xxx.com/t/article 		module ---> article

	// http://www.xxx.com/t/article-tag1 	module ---> article ,slug--->tag1
	// http://www.xxx.com/t/article-1 		module ---> article ,page--->1

	// http://www.xxx.com/t/article-tag1-1 	module ---> article ,slug-tag1, page--->1
	// http://www.xxx.com/t/article-1-1 	module ---> article, slug-tag1, page--->1

	public void index() {
		Module module = tryToGetModule();
		if (module == null) {
			renderError(404);
			return;
		}

		Taxonomy taxonomy = null;
		try {
			taxonomy = tryToGetTaxonomy();
		} catch (Exception e) {
		}
		int pageNumber = tryToGetPageNumber();

		setAttr("pageNumber", pageNumber);
		setAttr("taxonomy", taxonomy);

		if (null == taxonomy) {
			render(String.format("taxonomy_%s.html", module.getName()));
		} else {
			render(String.format("taxonomy_%s_%s.html", module.getName(), taxonomy.getSlug()));
		}

	}

	private Taxonomy tryToGetTaxonomy() throws UnsupportedEncodingException {
		if (getParaCount() == 2) { // 2 para
			if (StringUtils.toInt(getPara(1), 0) != 0) { //
				return null;
			}
			return Taxonomy.DAO.findBySlug(URLDecoder.decode(getPara(1),"utf-8"));
		}

		if (getParaCount() >= 3) { // 3 para
			long id = StringUtils.toLong(getPara(1), (long) 0);
			return id > 0 ? Taxonomy.DAO.findById(id) : Taxonomy.DAO.findBySlug(URLDecoder.decode(getPara(1),"utf-8"));
		}

		return null;
	}

	private Module tryToGetModule() {
		return Jpress.currentTemplate().getModuleByName(getPara(0));
	}

	private int tryToGetPageNumber() {
		return StringUtils.toInt(getPara(getParaCount() - 1), 1);
	}

}
