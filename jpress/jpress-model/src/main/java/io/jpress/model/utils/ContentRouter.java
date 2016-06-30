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
package io.jpress.model.utils;

import java.text.SimpleDateFormat;

import io.jpress.Consts;
import io.jpress.model.Content;
import io.jpress.model.query.OptionQuery;
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


	public static String getRouter(Content content) {

		String url = getRouterWithoutSuffix(content);

		String settingType = getRouterType();
		if (TYPE_DYNAMIC_ID.equals(settingType) || TYPE_DYNAMIC_SLUG.equals(settingType)) {
			return url;
		}

		if (enalbleFakeStatic()) {
			url += getFakeStaticSuffix();
		}
		return url;
	}

	public static String getRouter(Content content, int pageNumber) {
		String url = getRouterWithoutSuffix(content);

		String settingType = getRouterType();
		if (TYPE_DYNAMIC_ID.equals(settingType) || TYPE_DYNAMIC_SLUG.equals(settingType)) {
			return url + "&p=" + pageNumber;
		}

		if (enalbleFakeStatic()) {
			return url + URL_PARA_SEPARATOR + pageNumber + getFakeStaticSuffix();
		}
		return url + URL_PARA_SEPARATOR + pageNumber;
	}

	private static String getRouterWithoutSuffix(Content content) {
		String settingType = getRouterType();
		// 模型SLUG
		if (TYPE_STATIC_MODULE_SLUG.equals(settingType)) {
			return SLASH + content.getModule() + SLASH + content.getSlug();
		}
		// 模型ID
		else if (TYPE_STATIC_MODULE_ID.equals(settingType)) {
			return SLASH + content.getModule() + SLASH + content.getId();
		}
		// 日期SLUG
		else if (TYPE_STATIC_DATE_SLUG.equals(settingType)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return SLASH + sdf.format(content.getCreated()) + SLASH + content.getSlug();
		}
		// 日期ID
		else if (TYPE_STATIC_DATE_ID.equals(settingType)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return SLASH + sdf.format(content.getCreated()) + SLASH + content.getId();
		}
		// 前缀SLUG
		else if (TYPE_STATIC_PREFIX_SLUG.equals(settingType)) {
			String prefix = getRouterPrefix();
			return SLASH + prefix + SLASH + content.getSlug();
		}
		// 前缀ID
		else if (TYPE_STATIC_PREFIX_ID.equals(settingType)) {
			String prefix = getRouterPrefix();
			return SLASH + prefix + SLASH + content.getId();
		}
		// 动态ID
		else if (TYPE_DYNAMIC_ID.equals(settingType)) {
			String prefix = getRouterPrefix();
			return SLASH + prefix + "?id=" + content.getId();
		} 
		// 动态SLUG
		else if (TYPE_DYNAMIC_SLUG.equals(settingType)) {
			String prefix = getRouterPrefix();
			return SLASH + prefix + "?slug=" + content.getSlug();
		}
		else {
			return Consts.ROUTER_CONTENT + "?id=" + content.getId();
		}
	}

	public static String getRouterType() {
		String type = OptionQuery.findValue("router_content_type");
		if (!StringUtils.isNotBlank(type))
			return DEFAULT_TYPE;

		return type;
	}

	public static String getRouterPrefix() {
		String prefix = OptionQuery.findValue("router_content_prefix");
		if (!StringUtils.isNotBlank(prefix))
			prefix = Consts.ROUTER_CONTENT.substring(1);
		return prefix;
	}

	public static String getContentRouterSuffix(String moduleName) {
		if (Consts.MODULE_PAGE.equals(moduleName)) {
			if (enalbleFakeStatic()) {
				return getFakeStaticSuffix();
			}
			return "";
		}else{
			String routerType = getRouterType();
			if (TYPE_DYNAMIC_ID.equals(routerType) || TYPE_DYNAMIC_SLUG.equals(routerType) ) {
				return "";
			}else{
				if (enalbleFakeStatic()) {
					return getFakeStaticSuffix();
				}
				return "";
			}
		}
	}

	public static String getContentRouterPreffix(String moduleName) {

		if (Consts.MODULE_PAGE.equals(moduleName)) {
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
			String router_content_prefix = DateUtils.DateString();
			urlPreffix = SLASH + router_content_prefix + SLASH;
		}

		else if (TYPE_STATIC_MODULE_SLUG.equals(routerType)) {
			String router_content_prefix = moduleName;
			urlPreffix = SLASH + router_content_prefix + SLASH;
		} else {
			String router_content_prefix = getRouterPrefix();
			urlPreffix = SLASH + router_content_prefix + "?id=";
		}
		return urlPreffix;
	}

}
