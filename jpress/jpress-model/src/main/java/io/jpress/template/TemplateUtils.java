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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;

import io.jpress.model.Option;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public class TemplateUtils {

	private static Map<String, Boolean> cache = new ConcurrentHashMap<String, Boolean>();

	private static boolean exists(String path) {
		Boolean result = cache.get(path);
		if (null == result || !result) {
			result = new File(path).exists();
			cache.put(path, result);
		}
		return result;
	}

	public static boolean existsFile(String fileName) {
		String viewPath = getTemplatePath() + File.separator + fileName;
		return exists(PathKit.getWebRootPath() + viewPath);
	}

	public static String getcurrentTemplateId() {
		String templateId = OptionQuery.me().findValue(Option.KEY_TEMPLATE_ID);

		if (StringUtils.isNotBlank(templateId)) {
			return templateId;
		}

		return PropKit.get("default_template");
	}

	private static Template cTemplate;

	public static Template currentTemplate() {
		if (cTemplate == null) {

			String templateId = getcurrentTemplateId();
			List<Template> templateList = TemplateUtils.scanTemplates();

			for (Template tpl : templateList) {
				if (templateId.equals(tpl.getId())) {
					cTemplate = tpl;
				}
			}
		}

		return cTemplate;
	}

	public static boolean templateChange(String templateId) {
		List<Template> templateList = TemplateUtils.scanTemplates();

		if (StringUtils.isBlank(templateId) || templateList == null || templateList.isEmpty()) {
			return false;
		}

		Template template = null;
		for (Template tpl : templateList) {
			if (templateId.equals(tpl.getId())) {
				template = tpl;
			}
		}

		if (template != null) {
			OptionQuery.me().saveOrUpdate(Option.KEY_TEMPLATE_ID, template.getId());
			cTemplate = null;
			return true;
		}
		return false;
	}

	public static List<Template> scanTemplates() {
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

	private static void scanTemplateFloders(File file, List<File> fillToList) {
		if (file.isDirectory()) {

			File configFile = new File(file, "tpl_config.xml");

			if (configFile.exists() && configFile.isFile()) {
				fillToList.add(file);
			} else {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File f : files) {
						if (f.isDirectory())
							scanTemplateFloders(f, fillToList);
					}
				}
			}
		}
	}

	public static String getTemplatePath() {
		return currentTemplate().getPath();
	}

	public static String[] getCurrentTemplateModulesAsArray() {
		List<TplModule> list = currentTemplate().getModules();
		String[] modules = new String[list.size()];
		for (int i = 0; i < modules.length; i++) {
			modules[i] = list.get(i).getName();
		}
		return modules;
	}

}
