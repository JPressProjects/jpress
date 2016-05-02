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

import java.math.BigInteger;

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

	// http://www.xxx.com/t/article module ---> article

	// http://www.xxx.com/t/article-tag1 module ---> article ,slug--->tag1
	// http://www.xxx.com/t/article-1 module ---> article ,page--->1

	// http://www.xxx.com/t/article-tag1-1 module ---> article ,slug-tag1,
	// page--->1
	// http://www.xxx.com/t/article-1-1 module ---> article, id-1, page--->1

	public void index() {
		String moduleName = getPara(0);
		if (moduleName == null) {
			renderError(404);
			return;
		}

		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		if (module == null) {
			renderError(404);
			return;
		}

		if (getParaCount() >= 2) {
			if (getPara(1) == null) {
				renderError(404);
				return;
			}
		}

		Taxonomy taxonomy = null;
		if (getParaCount() == 2) { // 2 para

			// the 2th para is not number
			if (StringUtils.toInt(getPara(1), 0) == 0) {
				taxonomy = Taxonomy.DAO.findBySlugAndModule(StringUtils.urlDecode(getPara(1)), moduleName);
				if (null == taxonomy) {
					renderError(404);
					return;
				}
			}
		} else if (getParaCount() >= 3) { // 3 para

			BigInteger id = StringUtils.toBigInteger(getPara(0), BigInteger.ZERO);
			if (id.compareTo(BigInteger.ZERO) > 0) {
				taxonomy = Taxonomy.DAO.findById(id);
			} else {
				taxonomy = Taxonomy.DAO.findBySlugAndModule(StringUtils.urlDecode(getPara(1)), moduleName);
			}
			if (null == taxonomy) {
				renderError(404);
				return;
			}
		}

		int pageNumber = StringUtils.toInt(getPara(getParaCount() - 1), 1);

		setAttr(Consts.ATTR_PAGE_NUMBER, pageNumber);
		setAttr("taxonomy", taxonomy);
		setAttr("module", module);
		setAttr("PAGE_URL", getPageUrl(moduleName, taxonomy));

		if (null == taxonomy) {
			render(String.format("taxonomy_%s.html", module.getName()));
		} else {
			render(String.format("taxonomy_%s_%s.html", module.getName(), taxonomy.getSlug()));
		}

	}

	private Object getPageUrl(String moduleName, Taxonomy taxonomy) {
		String url = "/" + moduleName;
		return taxonomy == null ? url + "/" : url + "/" + taxonomy.getSlug() + "-";
	}

}
