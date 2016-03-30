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

import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Taxonomy;
import io.jpress.template.Module;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/t")
public class TaxonomyController extends BaseFrontController {

	// http://www.xxx.com/t/module-slug-pageNumber.html
	// http://www.xxx.com/t/module-id-pageNumber.html
	// http://www.xxx.com/t/article-slug1-1.html

	// http://www.xxx.com/article/slug1-1.html taxonomy:slug1 page:1
	// http://www.xxx.com/article/1-1.html taxonomy:slug1 page:1
	// http://www.xxx.com/article/1.html taxonomy:all page:1

	public void index() {
		Module module = tryToGetModule();
		if (module == null) {
			renderError(404);
			return;
		}

		int pageNumber = tryToGetPageNumber();
		Taxonomy taxonomy = tryToGetTaxonomy();

		setAttr("pageNumber", pageNumber);
		setAttr("taxonomy", taxonomy);

		if (null == taxonomy) {
			render(String.format("taxonomy_%s.html", module.getName()));
		} else {
			render(String.format("taxonomy_%s_%s.html", module.getName(), taxonomy.getSlug()));
		}

	}

	private Taxonomy tryToGetTaxonomy() {
		if (StringUtils.toInt(getPara(2), 0) == 0) { // no pageNumber
			return null;
		}
		long id = StringUtils.toLong(getPara(0), (long) 0);
		return id > 0 ? Taxonomy.DAO.findById(id) : Taxonomy.DAO.findBySlug(getPara(0));
	}
	

	private Module tryToGetModule() {
		return Jpress.currentTemplate().getModuleByName(getPara(0));
	}
	

	private int tryToGetPageNumber() {
		int pageNumber = StringUtils.toInt(getPara(2), 0);
		return pageNumber > 0 ? pageNumber : StringUtils.toInt(getPara(1),1);
	}

}
