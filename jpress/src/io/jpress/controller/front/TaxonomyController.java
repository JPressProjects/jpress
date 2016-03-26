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

import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Taxonomy;

@UrlMapping(url = "/t")
public class TaxonomyController extends BaseFrontController {

	//  http://www.xxx.com/t/123.html   taxonomy.id:123  page:1
	//  http://www.xxx.com/t/123-1.html
	
	// http://www.xxx.com/t/article.html 	taxonomy.slug:article  page:1
	// http://www.xxx.com/t/article-1.html
	
	public void index() {
		Taxonomy taxonomy = tryToGetTaxonomy();
		if (taxonomy == null) {
			renderError(404);
			return;
		}

		int pageNumber = getParaToInt(1, 1);
		setAttr("pageNumber", pageNumber);
		setAttr("taxonomy", taxonomy);
		
		render(String.format("taxonomy_%s.html", taxonomy.getSlug()));
	}


	private Taxonomy tryToGetTaxonomy() {
		long id = getParaToLong(0, (long) 0);
		return id > 0 ? Taxonomy.DAO.findById(id) : Taxonomy.DAO.findBySlug(getPara(0));
	}

}
