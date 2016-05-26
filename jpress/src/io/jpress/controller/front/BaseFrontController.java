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

import io.jpress.core.JBaseController;
import io.jpress.core.Jpress;
import io.jpress.template.TemplateUtils;

public class BaseFrontController extends JBaseController {

	private static final String FILE_SEPARATOR = "_";

	public void render(String name) {
		String originalName = name;
		if (templateExists(name)) {
			renderTemplate(name);
			return;
		}

		if (name.indexOf(FILE_SEPARATOR) != -1) {
			do {
				if (templateExists(name)) {
					break;
				}
				name = clearProp(name);
			} while (name.indexOf(FILE_SEPARATOR) != -1);
		}

		if (templateExists(name)) {
			renderTemplate(name);
		} else {
			renderError(originalName);
		}
	}

	private void renderError(String name) {
//		List<String> list = new ArrayList<String>();
//		list.add(name);
//		
//		if (name.indexOf(FILE_SEPARATOR) != -1) {
//			do {
//				name = clearProp(name);
//				list.add(name);
//			} while (name.indexOf(FILE_SEPARATOR) != -1);
//		}
//		
//		String pagesName = Arrays.toString(list.toArray());
//		renderText(String.format("html page \"%s\" not found in template \"%s\".", pagesName,
//				TemplateUtils.getTemplateName()));
		
		renderError(404);
	}

	private void renderTemplate(String name) {
		super.render(Jpress.currentTemplate().getPath() + "/" + name);
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf(FILE_SEPARATOR)) + ".html";
	}

	private boolean templateExists(String htmlFileName) {
		return TemplateUtils.existsFile(htmlFileName);
	}

}
