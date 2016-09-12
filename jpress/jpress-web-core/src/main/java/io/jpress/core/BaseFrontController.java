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
	private String renderFile = null;

	public void render(String name) {

		renderFile = null;

		initRenderFile(name);

		if (renderFile != null) {
			super.render(renderFile);
		} else {
			renderError(404);
		}

	}

	private void initRenderFile(String name) {
		if (isWechatBrowser()) {
			initWechatFile(name);

			if (renderFile == null) {
				initMobileFile(name);
			}
		}

		if (isMoblieBrowser()) {
			initMobileFile(name);
		}

		if (renderFile == null) {
			initNormalFile(name);
		}
	}

	public boolean templateExists(String file) {
		boolean isExists = false;
		if (isWechatBrowser()) {
			isExists = TemplateManager.me().existsFileInWechat(file);

			if (!isExists) {
				isExists = TemplateManager.me().existsFileInMobile(file);
			}
		}

		if (isMoblieBrowser()) {
			isExists = TemplateManager.me().existsFileInMobile(file);
		}

		if (!isExists) {
			isExists = TemplateManager.me().existsFile(file);
		}

		return isExists;
	}

	private void initWechatFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFileInWechat(name)) {
					renderFile = buildWechatPath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFileInWechat(name)) {
			renderFile = buildWechatPath(name);
		}
	}

	private void initMobileFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFileInMobile(name)) {
					renderFile = buildMobilePath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFileInMobile(name)) {
			renderFile = buildMobilePath(name);
		}
	}

	private void initNormalFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFile(name)) {
					renderFile = buildPath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFile(name)) {
			renderFile = buildPath(name);
			return;
		}

	}

	private String buildPath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/" + name;
	}

	private String buildWechatPath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/tpl_wechat/" + name;
	}

	private String buildMobilePath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/tpl_mobile/" + name;
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf(FILE_SEPARATOR)) + ".html";
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
