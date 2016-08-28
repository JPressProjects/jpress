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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.Consts;
import io.jpress.model.query.OptionQuery;
import io.jpress.router.RouterConverter;
import io.jpress.template.TplModule;
import io.jpress.template.TemplateManager;
import io.jpress.utils.DateUtils;
import io.jpress.utils.StringUtils;

public class ContentRouter extends RouterConverter {

	public static final String TYPE_STATIC_MODULE_SLUG = "_static_module_slug"; // 模型SLUG
	public static final String TYPE_STATIC_MODULE_ID = "_static_module_id"; // 静态模型ID
	public static final String TYPE_STATIC_DATE_SLUG = "_static_date_slug"; // 静态日期slug
	public static final String TYPE_STATIC_DATE_ID = "_static_date_id"; // 静态日期id
	public static final String TYPE_STATIC_PREFIX_SLUG = "_static_prefix_slug"; // 静态slug
	public static final String TYPE_STATIC_PREFIX_ID = "_static_prefix_id"; // 静态ID

	public static final String TYPE_DYNAMIC_ID = "_dynamic_id"; // 动态类型
	public static final String TYPE_DYNAMIC_SLUG = "_dynamic_slug"; // 动态类型

	public static final String DEFAULT_TYPE = TYPE_STATIC_PREFIX_SLUG;

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response) {
		
		String[] targetDirs = parseTarget(target);

		if (targetDirs == null || targetDirs.length == 0) {
			return null;
		}

		else if (targetDirs != null && targetDirs.length == 1) {
			String settingType = getRouterType();
			// 动态前缀
			if (TYPE_DYNAMIC_ID.equals(settingType) || TYPE_DYNAMIC_SLUG.equals(settingType)) {
				String prefix = getRouterPrefix();
				return prefix.equals(targetDirs[0]) ? Consts.ROUTER_CONTENT : null;
			}

			return null;
		}

		String[] params = parseParam(targetDirs[1]);
		if (params == null || params.length == 0) {
			return null;
		}

		String settingType = getRouterType();
		// 静态模型
		if (TYPE_STATIC_MODULE_SLUG.equals(settingType) || TYPE_STATIC_MODULE_ID.equals(settingType)) {
			TplModule m = TemplateManager.me().currentTemplateModule(targetDirs[0]);
			return m == null ? null : Consts.ROUTER_CONTENT + SLASH + targetDirs[1];
		}
		// 静态日期
		else if (TYPE_STATIC_DATE_SLUG.equals(settingType) || TYPE_STATIC_DATE_ID.equals(settingType)) {
			try {
				Integer.valueOf(targetDirs[0]);
				return Consts.ROUTER_CONTENT + SLASH + targetDirs[1];
			} catch (Exception e) {
			}
			return null;
		}
		// 静态前缀
		else if (TYPE_STATIC_PREFIX_SLUG.equals(settingType) || TYPE_STATIC_PREFIX_ID.equals(settingType)) {
			String prefix = getRouterPrefix();
			return prefix.equals(targetDirs[0]) ? Consts.ROUTER_CONTENT + SLASH + targetDirs[1] : null;
		}

		return null;
	}

	public static String getRouterType() {
		String type = OptionQuery.me().findValue("router_content_type");
		if (StringUtils.isBlank(type))
			return DEFAULT_TYPE;

		return type;
	}

	public static String getRouterPrefix() {
		String prefix = OptionQuery.me().findValue("router_content_prefix");
		if (StringUtils.isBlank(prefix))
			prefix = Consts.ROUTER_CONTENT.substring(1);
		return prefix;
	}

	public static String getContentRouterSuffix(TplModule module) {
		if (Consts.MODULE_PAGE.equals(module.getName())) {
			if (enalbleFakeStatic()) {
				return getFakeStaticSuffix();
			}
			return "";
		} else {
			String routerType = getRouterType();
			if (TYPE_DYNAMIC_ID.equals(routerType) || TYPE_DYNAMIC_SLUG.equals(routerType)) {
				return "";
			} else {
				if (enalbleFakeStatic()) {
					return getFakeStaticSuffix();
				}
				return "";
			}
		}
	}

	public static String getContentRouterPreffix(TplModule module) {

		if (Consts.MODULE_PAGE.equals(module.getName())) {
			return SLASH;
		}

		String urlPreffix = "";
		String routerType = getRouterType();
		if (TYPE_DYNAMIC_ID.equals(routerType)) {
			String router_content_prefix = getRouterPrefix();
			urlPreffix = SLASH + router_content_prefix + "?id=";
		}

		else if (TYPE_DYNAMIC_SLUG.equals(routerType)) {
			String router_content_prefix = getRouterPrefix();
			urlPreffix = SLASH + router_content_prefix + "?slug=";
		}

		else if (TYPE_STATIC_PREFIX_SLUG.equals(routerType)) {
			String router_content_prefix = getRouterPrefix();
			urlPreffix = SLASH + router_content_prefix + SLASH;
		}

		else if (TYPE_STATIC_DATE_SLUG.equals(routerType)) {
			String router_content_prefix = DateUtils.dateString();
			urlPreffix = SLASH + router_content_prefix + SLASH;
		}

		else if (TYPE_STATIC_MODULE_SLUG.equals(routerType)) {
			String router_content_prefix = module.getName();
			urlPreffix = SLASH + router_content_prefix + SLASH;
		} else {
			String router_content_prefix = getRouterPrefix();
			urlPreffix = SLASH + router_content_prefix + "?id=";
		}
		return urlPreffix;
	}

}
