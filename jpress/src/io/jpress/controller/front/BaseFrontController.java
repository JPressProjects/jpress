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
import io.jpress.template.TemplateUtils;

import com.jfinal.kit.PathKit;

public class BaseFrontController extends JBaseController {
	private static final String T_FORMAT = "/templates/%s/%s";

	public void render(String name) {
		if(templateExists(name)){
			renderTemplate(name);
			return;
		}
		
		if(name.indexOf("_") !=- 1){
			do {
				if (templateExists(name)) {
					break;
				}
				name = clearProp(name);
			} while (name.indexOf("_") !=- 1);
		}

		if (templateExists(name)) {
			renderTemplate(name);
		} else {
			renderError(name);
		}
	}

	private void renderError(String name) {
		renderText(String.format(
				"there is no \"%s\" file in template \"%s\".", name,
				TemplateUtils.getTemplateName()));
	}

	private void renderTemplate(String name) {
		super.render(String.format(T_FORMAT,
				TemplateUtils.getTemplateName(), name));
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf("_")) + ".html";
	}

	private boolean templateExists(String htmlFileName) {
		String tName = TemplateUtils.getTemplateName();
		String htmlPath = String.format(T_FORMAT, tName, htmlFileName);
		return TemplateUtils.exists(PathKit.getWebRootPath() + htmlPath);
	}

}
