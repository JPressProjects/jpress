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

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.Consts;
import io.jpress.core.Jpress;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.router.RouterConverter;
import io.jpress.template.Module;
import io.jpress.utils.DateUtils;
import io.jpress.utils.StringUtils;

public class ContentRouter extends RouterConverter {

	public static final String TYPE_STATIC_MODULE = "_static_module"; // 静态模型
	public static final String TYPE_STATIC_DATE = "_static_date"; // 静态日期
	public static final String TYPE_STATIC_PREFIX = "_static_prefix"; // 静态前缀
	public static final String TYPE_DYNAMIC_ID = "_dynamic_id"; // 动态类型
	public static final String TYPE_DYNAMIC_SLUG = "_dynamic_slug"; // 动态类型

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response) {

		String[] targetDirs = parseTarget(target);

		if (targetDirs == null || targetDirs.length == 0) {
			return null;
		}

		else if (targetDirs != null && targetDirs.length == 1) {
			String settingType = getSettingType();
			// 动态前缀
			if (TYPE_DYNAMIC_ID.equals(settingType) || TYPE_DYNAMIC_SLUG.equals(settingType)) {
				String prefix = getSettignPrefix();
				return prefix.equals(targetDirs[0]) ? Consts.ROUTER_CONTENT : null;
			}

			return null;
		}

		String[] params = parseParam(targetDirs[1]);
		if (params == null || params.length == 0) {
			return null;
		}

		String settingType = getSettingType();
		// 静态模型
		if (TYPE_STATIC_MODULE.equals(settingType)) {
			Module m = Jpress.currentTemplate().getModuleByName(targetDirs[0]);
			return m == null ? null : Consts.ROUTER_CONTENT + SLASH + targetDirs[1];
		}
		// 静态日期
		else if (TYPE_STATIC_DATE.equals(settingType)) {
			try {
				Integer.valueOf(targetDirs[0]);
				return Consts.ROUTER_CONTENT + SLASH + targetDirs[1];
			} catch (Exception e) {
			}
			return null;
		}
		// 静态前缀
		else if (TYPE_STATIC_PREFIX.equals(settingType)) {
			String prefix = getSettignPrefix();
			return prefix.equals(targetDirs[0]) ? Consts.ROUTER_CONTENT + SLASH + targetDirs[1] : null;
		}

		return null;
	}

	public static String getRouter(Content content) {

		String url = getRouterWithoutSuffix(content);

		String settingType = getSettingType();
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

		String settingType = getSettingType();
		if (TYPE_DYNAMIC_ID.equals(settingType) || TYPE_DYNAMIC_SLUG.equals(settingType)) {
			return url + "&pageNumber=" + pageNumber;
		}

		if (enalbleFakeStatic()) {
			return url + URL_PARA_SEPARATOR + pageNumber + getFakeStaticSuffix();
		}
		return url + URL_PARA_SEPARATOR + pageNumber;
	}

	private static String getRouterWithoutSuffix(Content content) {
		String settingType = getSettingType();
		String slugOrId = content.getSlug() != null ? content.getSlug() : content.getId().toString();
		// 静态模型
		if (TYPE_STATIC_MODULE.equals(settingType)) {
			return SLASH + content.getModule() + SLASH + slugOrId;
		}
		// 静态日期
		else if (TYPE_STATIC_DATE.equals(settingType)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return SLASH + sdf.format(content.getCreated()) + SLASH + slugOrId;
		}
		// 静态前缀
		else if (TYPE_STATIC_PREFIX.equals(settingType)) {
			String prefix = getSettignPrefix();
			return SLASH + prefix + SLASH + slugOrId;
		}
		// 动态ID
		else if (TYPE_DYNAMIC_ID.equals(settingType)) {
			String prefix = getSettignPrefix();
			return SLASH + prefix + "?id=" + content.getId();
		} 
		// 动态SLUG
		else if (TYPE_DYNAMIC_SLUG.equals(settingType)) {
			String prefix = getSettignPrefix();
			return SLASH + prefix + "?slug=" + content.getSlug();
		}
		else {
			return Consts.ROUTER_CONTENT + "?id=" + content.getId();
		}
	}

	public static String getSettingType() {
		String type = Option.findValue("router_content_type");
		if (type == null)
			return TYPE_STATIC_MODULE;

		return type;
	}

	public static String getSettignPrefix() {
		String prefix = Option.findValue("router_content_prefix");
		if (!StringUtils.isNotBlank(prefix))
			prefix = Consts.ROUTER_CONTENT;
		return prefix;
	}

	public static String getContentRouterSuffix(Module module) {
		if (Consts.MODULE_PAGE.equals(module.getName())) {
			if (enalbleFakeStatic()) {
				return getFakeStaticSuffix();
			}
			return "";
		}else{
			String routerType = Option.findValue("router_content_type");
			if (TYPE_DYNAMIC_ID.equals(routerType) || TYPE_DYNAMIC_SLUG.equals(routerType)) {
				return "";
			}else{
				if (enalbleFakeStatic()) {
					return getFakeStaticSuffix();
				}
				return "";
			}
		}
		
	}

	public static String getContentRouterPreffix(Module module) {

		if (Consts.MODULE_PAGE.equals(module.getName())) {
			return "/";
		}
		
		String urlPreffix = "";
		String routerType = Option.findValue("router_content_type");
		if (TYPE_DYNAMIC_ID.equals(routerType)) {
			String router_content_prefix = Option.findValue("router_content_prefix");
			urlPreffix = "/" + router_content_prefix + "?id=";
		}
		
		else if (TYPE_DYNAMIC_SLUG.equals(routerType)) {
			String router_content_prefix = Option.findValue("router_content_prefix");
			urlPreffix = "/" + router_content_prefix + "?slug=";
		}

		else if (TYPE_STATIC_PREFIX.equals(routerType)) {
			String router_content_prefix = Option.findValue("router_content_prefix");
			if (!StringUtils.isNotBlank(router_content_prefix)) {
				urlPreffix = Consts.ROUTER_CONTENT + "/";
			} else {
				urlPreffix = "/" + router_content_prefix + "/";
			}
		}

		else if (TYPE_STATIC_DATE.equals(routerType)) {
			String router_content_prefix = DateUtils.DateString();
			urlPreffix = "/" + router_content_prefix + "/";
		}

		else if (TYPE_STATIC_MODULE.equals(routerType)) {
			String router_content_prefix = module.getName();
			urlPreffix = "/" + router_content_prefix + "/";
		} else {
			String router_content_prefix = Option.findValue("router_content_prefix");
			urlPreffix = "/" + router_content_prefix + "?id=";
		}
		return urlPreffix;
	}

}
