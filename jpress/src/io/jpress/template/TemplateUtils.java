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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;

import io.jpress.core.Jpress;
import io.jpress.model.Option;

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
		String viewPath = getTemplatePath()+File.separator+fileName;
		return exists(PathKit.getWebRootPath()+viewPath);
	}

	public static String getTemplateName() {
		String templateName = Option.findTemplateName();

		if (null != templateName && !"".equals(templateName.trim())) {
			return templateName;
		}

		if (Jpress.isDevMode()) {
			return "default";
		} else {
			return PropKit.get("default_template");
		}
	}
	
	public static String getTemplatePath(){
		return Jpress.currentTemplate().getPath();
	}

}
