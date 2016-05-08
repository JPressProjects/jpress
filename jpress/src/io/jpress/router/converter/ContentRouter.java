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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;

import io.jpress.Consts;
import io.jpress.core.Jpress;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.router.IRouterConverter;
import io.jpress.router.RouterParams;
import io.jpress.template.Module;
import io.jpress.utils.StringUtils;

public class ContentRouter implements IRouterConverter {

	private static final String URL_PARA_SEPARATOR = JFinal.me().getConstants().getUrlParaSeparator();
	private static final String SLASH = "/";

	public static final String TYPE_STATIC_MODULE = "_static_module"; // 静态模型
	public static final String TYPE_STATIC_DATE = "_static_date"; // 静态日期
	public static final String TYPE_STATIC_PREFIX = "_static_prefix"; // 静态前缀
	public static final String TYPE_DYNAMIC = "_dynamic"; // 动态类型

	// http://www.xxx.com/c/123_123

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response, Boolean[] bools) {

		String[] targetDirs = parseTarget(target);
		if (targetDirs == null || targetDirs.length == 0) {
			return null;
		}

		else if (targetDirs != null && targetDirs.length == 1) {
			String settingType = getSettingType();
			// 动态前缀
			if (TYPE_DYNAMIC.equals(settingType)) {
				String prefix = getSettignPrefix();
				return prefix.equals(targetDirs[0]) ? Consts.ROUTER_CONTENT : null;
			} else {
				return null;
			}
		}

		String[] params = parseParam(targetDirs[1]);
		if (params == null || params.length == 0) {
			return null;
		}

		String settingType = getSettingType();
		// 静态模型
		if (TYPE_STATIC_MODULE.equals(settingType)) {
			Module m = Jpress.currentTemplate().getModuleByName(targetDirs[0]);
			return m == null ? null : processTarget(request, bools, params);
		}
		// 静态日期
		else if (TYPE_STATIC_DATE.equals(settingType)) {
			return processTarget(request, bools, params);
		}
		// 静态前缀
		else if (TYPE_STATIC_PREFIX.equals(settingType)) {
			String prefix = getSettignPrefix();
			return prefix.equals(targetDirs[0]) ? processTarget(request, bools, params) : null;
		}

		return null;
	}

	public static String getRouter(Content content) {
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
		// 动态前缀
		else if (TYPE_DYNAMIC.equals(settingType)) {
			String prefix = getSettignPrefix();
			return SLASH + prefix + "?id" + content.getId();
		} else {
			return Consts.ROUTER_CONTENT + "?id=" + content.getId();
		}
	}

	private String processTarget(HttpServletRequest request, Boolean[] bools, String[] params) {
		bools[0] = true;
		buildAttr(request, params);
		return Consts.ROUTER_CONTENT;
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

	private static void buildAttr(HttpServletRequest request, String[] params) {
		RouterParams rp = new RouterParams();
		for (int i = 0; i < params.length; i++) {
			switch (i) {
			case 0:
				BigInteger id = tryGetBigInteger(params[i]);
				if (id != null)
					rp.id(id);
				else
					rp.slug(params[i]);
				break;
			case 1:
				rp.pageNumber(params[i]);
				break;
			case 2:
				rp.pageSize(params[i]);
				break;
			default:
				break;
			}
		}
		request.setAttribute(Consts.ATTR_ROUTER_ATTRS_MAP, rp);
	}

	private static BigInteger tryGetBigInteger(String param) {
		try {
			return new BigInteger(param);
		} catch (Exception e) {
		}
		return null;
	}

	private static String[] parseTarget(String target) {
		String[] strings = target.split(SLASH);
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
