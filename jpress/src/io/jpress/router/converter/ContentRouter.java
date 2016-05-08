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
package io.jpress.router.converter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;

import io.jpress.Consts;
import io.jpress.core.Jpress;
import io.jpress.model.Option;
import io.jpress.router.IRouterConverter;
import io.jpress.template.Module;
import io.jpress.utils.StringUtils;

public class ContentRouter implements IRouterConverter {

	private static final String URL_PARA_SEPARATOR = JFinal.me().getConstants().getUrlParaSeparator();

	public static final String TYPE_STATIC_MODULE = "_static_module"; // 静态模型
	public static final String TYPE_STATIC_DATE = "_static_date"; // 静态日期
	public static final String TYPE_STATIC_PREFIX = "_static_prefix"; // 静态前缀
	public static final String TYPE_DYNAMIC = "_dynamic"; // 动态类型

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response, Boolean[] bools) {

		String[] targetDirs = parseTarget(target);
		if (targetDirs == null || targetDirs.length != 2)
			return null;

		String[] params = parseParam(targetDirs[1]);

		String type = getSettingType();
		// 静态模型
		if (TYPE_STATIC_MODULE.equals(type)) {
			Module m = Jpress.currentTemplate().getModuleByName(targetDirs[0]);
			if (m != null) {
				bools[0] = true;
				buildAttr(request);
				return Consts.ROUTER_CONTENT;
			}
		}
		// 静态日期
		else if (TYPE_STATIC_DATE.equals(type)) {

		}
		// 静态前缀
		else if (TYPE_STATIC_PREFIX.equals(type)) {

		}

		return null;
	}

	public static String getSettingType() {
		String type = Option.findValue("router_content_type");
		if (type == null)
			return TYPE_STATIC_MODULE;

		return type;
	}

	public static String getSettignPrefix() {
		String prefix = Option.findValue("router_content_prefix");
		if (prefix == null)
			prefix = Consts.ROUTER_CONTENT;
		return prefix;
	}

	public static void buildAttr(HttpServletRequest request) {

	}

	private static String[] parseTarget(String target) {
		String[] strings = target.split("/");
		List<String> arrays = new ArrayList<String>();
		for (String string : strings) {
			if (StringUtils.isNotBlank(string)) {
				arrays.add(string);
			}
		}
		return arrays.toArray(new String[] {});
	}

	private static String[] parseParam(String param) {
		String[] strings = param.split(URL_PARA_SEPARATOR);
		List<String> arrays = new ArrayList<String>();
		for (String string : strings) {
			if (StringUtils.isNotBlank(string)) {
				arrays.add(string);
			}
		}
		return arrays.toArray(new String[] {});
	}

}
