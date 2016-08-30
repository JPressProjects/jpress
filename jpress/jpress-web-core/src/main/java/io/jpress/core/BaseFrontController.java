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
package io.jpress.core;

import com.jfinal.core.Controller;

import io.jpress.template.TemplateManager;
import io.jpress.utils.StringUtils;

public class BaseFrontController extends JBaseController {

	private static final String FILE_SEPARATOR = "_";

	public void render(String name) {
		if (templateExists(name)) {
			renderTemplate(name);
			return;
		}

		if (name.indexOf(FILE_SEPARATOR) != -1) {
			do {
				if (templateExists(name)) {
					renderTemplate(name);
					return;
				}
				name = clearProp(name);
			} while (name.indexOf(FILE_SEPARATOR) != -1);
		}

		if (templateExists(name)) {
			renderTemplate(name);
		} else {
			renderError(404);
		}

	}

	private void renderTemplate(String name) {
		if (Jpress.isDevMode()) {
			System.out.println(String.format("render : template is \"%s\",template html is \"%s\"",
					TemplateManager.me().currentTemplate().getTitle(), name));
		}
		super.render(TemplateManager.me().currentTemplate().getPath() + "/" + name);
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf(FILE_SEPARATOR)) + ".html";
	}

	public boolean templateExists(String htmlFileName) {
		return TemplateManager.me().existsFile(htmlFileName);
	}

	@Override
	public Controller keepPara() {
		super.keepPara();

		String gotoUrl = getPara("goto");
		if (StringUtils.isNotBlank(gotoUrl)) {
			setAttr("goto", StringUtils.urlEncode(gotoUrl));
		}

		return this;
	}

}
