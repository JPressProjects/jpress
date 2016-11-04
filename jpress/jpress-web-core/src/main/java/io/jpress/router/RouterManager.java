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
package io.jpress.router;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.log.Log;

import io.jpress.core.Jpress;
import io.jpress.core.addon.HookInvoker;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.router.ContentRouter;
import io.jpress.model.router.PageRouter;
import io.jpress.model.router.RouterConverter;
import io.jpress.model.router.TaxonomyRouter;
import io.jpress.utils.StringUtils;

public class RouterManager {
	private static final Log log = Log.getLog(RouterManager.class);
	static String[] urlPara = {null};
	static List<RouterConverter> converters = new ArrayList<RouterConverter>();
	static{
		converters.add(new TaxonomyRouter());
		converters.add(new PageRouter());
		converters.add(new ContentRouter());
	}


	public static String converte(String target, HttpServletRequest request, HttpServletResponse response) {
		if (!Jpress.isInstalled()) {
			return target;
		}

		if ("/".equals(target)) {
			return target;
		}
		
		Action action = JFinal.me().getAction(target, urlPara);
		if (action != null) {
			RouterNotAllowConvert notAllowConvert = action.getControllerClass().getAnnotation(RouterNotAllowConvert.class);
			if (notAllowConvert != null) {
				return target;
			}
			
			notAllowConvert = action.getMethod().getAnnotation(RouterNotAllowConvert.class);
			if (notAllowConvert != null) {
				return target;
			}
		}

		String hookTarget = HookInvoker.routerConverte(target, request, response);
		if(StringUtils.isNotBlank(hookTarget)){
			return hookTarget;
		}
		

		if (target.indexOf('.') != -1) {
			Boolean fakeStaticEnable = OptionQuery.me().findValueAsBool("router_fakestatic_enable");
			if (fakeStaticEnable == null || !fakeStaticEnable) {
				return target;
			}

			String fakeStaticSuffix = OptionQuery.me().findValue("router_fakestatic_suffix");

			if (!StringUtils.isNotBlank(fakeStaticSuffix)) {
				fakeStaticSuffix = ".html";
			}

			int index = target.lastIndexOf(fakeStaticSuffix);
			if (-1 == index) {
				return target;
			}

			target = target.substring(0, index);
		}

		try {
			for (RouterConverter c : converters) {
				String newTarget = c.converter(target, request, response);
				if (newTarget != null) {
					if (Jpress.isDevMode()) {
						String formatString = "target\"%s\" was converted to \"%s\" by %s.(%s.java:1)";
						System.err.println(String.format(formatString, target, newTarget, c.getClass().getName(),
								c.getClass().getSimpleName()));
					}
					return newTarget;
				}
			}
		} catch (Exception e) {
			log.warn("IRouterConverter converter exception", e);
		}

		return target;

	}

}
