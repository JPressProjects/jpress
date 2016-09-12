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
package io.jpress.template;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;

import io.jpress.model.Option;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public class TemplateManager {

	private Template cTemplate;
	private List<String> cTemplateHtmls = new ArrayList<String>();
	private List<String> cWechatTemplateHtmls = new ArrayList<String>();
	private List<String> cMobileTemplateHtmls = new ArrayList<String>();

	private TemplateManager() {
	}

	private static final TemplateManager me = new TemplateManager();

	public static TemplateManager me() {
		return me;
	}

	public boolean existsFile(String fileName) {
		return cTemplateHtmls.contains(fileName);
	}

	public boolean existsFileInWechat(String fileName) {
		return cWechatTemplateHtmls.contains(fileName);
	}

	public boolean existsFileInMobile(String fileName) {
		return cMobileTemplateHtmls.contains(fileName);
	}

	public boolean isSupportWechat() {
		return cWechatTemplateHtmls.size() > 0;
	}

	public boolean isSupportMobile() {
		return cMobileTemplateHtmls.size() > 0;
	}

	public String currentTemplatePath() {
		return currentTemplate().getPath();
	}

	public List<TplModule> currentTemplateModules() {
		return currentTemplate().getModules();
	}

	public String[] currentTemplateModulesAsArray() {
		List<TplModule> list = currentTemplate().getModules();
		String[] modules = new String[list.size()];
		for (int i = 0; i < modules.length; i++) {
			modules[i] = list.get(i).getName();
		}
		return modules;
	}

	public TplModule currentTemplateModule(String name) {
		return currentTemplate().getModuleByName(name);
	}

	public Thumbnail currentTemplateThumbnail(String name) {
		return currentTemplate().getThumbnailByName(name);
	}

	public Template currentTemplate() {
		if (cTemplate == null) {

			List<Template> templateList = getTemplates();

			String templateId = OptionQuery.me().findValue(Option.KEY_TEMPLATE_ID);
			if (StringUtils.isNotBlank(templateId)) {
				for (Template tpl : templateList) {
					if (templateId.equals(tpl.getId())) {
						cTemplate = tpl;
					}
				}
			}

			if (cTemplate == null) {// 数据库没有配置过，或者配置不正确，比如曾经配置的模板被手动删除了
				templateId = PropKit.get("default_template");
			}

			if (StringUtils.isBlank(templateId)) {
				throw new RuntimeException("default_template config error in jpress.properties.");
			}

			for (Template tpl : templateList) {
				if (templateId.equals(tpl.getId())) {
					cTemplate = tpl;
				}
			}

			if (cTemplate == null) {
				throw new RuntimeException(
						"get current template error. please define correct template in jpress.properties.");
			}

			File tDir = new File(PathKit.getWebRootPath(), cTemplate.getPath());

			cTemplateHtmls.clear();
			cMobileTemplateHtmls.clear();
			cWechatTemplateHtmls.clear();

			scanFillTemplate(tDir, cTemplateHtmls);
			scanFillTemplate(new File(tDir, "tpl_mobile"), cMobileTemplateHtmls);
			scanFillTemplate(new File(tDir, "tpl_wechat"), cWechatTemplateHtmls);

		}

		return cTemplate;
	}

	private void scanFillTemplate(File tDir, List<String> templates) {
		if (tDir.exists() && tDir.isDirectory()) {
			File[] templateFiles = tDir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".html");
				}
			});
			if (templateFiles != null && templateFiles.length > 0) {
				for (int i = 0; i < templateFiles.length; i++) {
					templates.add(templateFiles[i].getName());
				}
			}
		}
	}

	public boolean doChangeTemplate(String templateId) {
		if (StringUtils.isBlank(templateId)) {
			return false;
		}

		List<Template> templateList = getTemplates();
		if (templateList == null || templateList.isEmpty()) {
			return false;
		}

		Template template = null;
		for (Template tpl : templateList) {
			if (templateId.equals(tpl.getId())) {
				template = tpl;
			}
		}

		if (template == null) {
			return false;
		}

		if (!OptionQuery.me().saveOrUpdate(Option.KEY_TEMPLATE_ID, template.getId())) {
			return false;
		}

		cTemplate = null;
		return true;

	}

	public List<Template> getTemplates() {
		String basePath = PathKit.getWebRootPath() + "/templates";

		List<File> templateFolderList = new ArrayList<File>();
		scanTemplateFloders(new File(basePath), templateFolderList);

		List<Template> templatelist = null;
		if (templateFolderList.size() > 0) {
			templatelist = new ArrayList<Template>();
			for (File templateFolder : templateFolderList) {
				templatelist.add(new TemplateConfigParser().parser(templateFolder));
			}
		}

		return templatelist;
	}

	private void scanTemplateFloders(File file, List<File> list) {
		if (file.isDirectory()) {

			File configFile = new File(file, "tpl_config.xml");

			if (configFile.exists() && configFile.isFile()) {
				list.add(file);
			} else {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File f : files) {
						if (f.isDirectory())
							scanTemplateFloders(f, list);
					}
				}
			}
		}
	}

}
