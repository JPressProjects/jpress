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
package io.jpress.plugin.router;

import io.jpress.core.Jpress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouterKit {

	static RouterConverterManager tcManager;

	static void init(RouterConverterManager tcm) {
		tcManager = tcm;
	}

	public static void register(Class<? extends IRouterConverter> clazz) {
		tcManager.register(clazz);
	}

	public static String converte(String target, HttpServletRequest request,HttpServletResponse response) {

		IRouterConverter converter = tcManager.match(target);
		if (null == converter) {
			return target;
		}

		String newTarget = converter.converter(target, request, response);

		if (Jpress.isDevMode()) {
			System.err.println(String.format(
					"target\"%s\" was converted to \"%s\" by %s.(%s.java:1)",
					target, newTarget, converter.getClass().getName(),
					converter.getClass().getSimpleName()));
		}

		return newTarget;

	}

}
