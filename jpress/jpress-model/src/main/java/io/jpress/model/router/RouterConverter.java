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
package io.jpress.model.router;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;

import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public abstract class RouterConverter {

	public static final String URL_PARA_SEPARATOR = JFinal.me().getConstants().getUrlParaSeparator();
	public static final String SLASH = "/";

	public abstract String converter(String target, HttpServletRequest request, HttpServletResponse response);

	public static BigInteger tryGetBigInteger(String param) {
		try {
			return new BigInteger(param);
		} catch (Exception e) {
		}
		return null;
	}

	public static String[] parseTarget(String target) {
		String[] strings = target.split(SLASH);
		List<String> arrays = new ArrayList<String>();
		for (String string : strings) {
			if (StringUtils.isNotBlank(string)) {
				arrays.add(string);
			}
		}
		return arrays.toArray(new String[] {});
	}

	public static String[] parseParam(String param) {
		String[] strings = param.split(URL_PARA_SEPARATOR);
		List<String> arrays = new ArrayList<String>();
		for (String string : strings) {
			if (StringUtils.isNotBlank(string)) {
				arrays.add(string);
			}
		}
		return arrays.toArray(new String[] {});
	}

	protected static boolean enalbleFakeStatic() {
		Boolean fakeStaticEnable = OptionQuery.me().findValueAsBool("router_fakestatic_enable");
		return fakeStaticEnable != null && fakeStaticEnable == true;
	}

	protected static String getFakeStaticSuffix() {
		String fakeStaticSuffix = OptionQuery.me().findValue("router_fakestatic_suffix");
		if (StringUtils.isNotBlank(fakeStaticSuffix)) {
			return fakeStaticSuffix.trim();
		}
		return ".html";
	}

}
