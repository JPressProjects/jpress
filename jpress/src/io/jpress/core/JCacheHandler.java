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
package io.jpress.core;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.render.RenderFactory;

import io.jpress.core.annotation.ActionCache;
import io.jpress.utils.StringUtils;

public class JCacheHandler extends Handler {

	static String[] urlPara = { null };
	static Log log = Log.getLog(JCacheHandler.class);

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		Action action = JFinal.me().getAction(target, urlPara);
		if (action == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		ActionCache actionCache = action.getMethod().getAnnotation(ActionCache.class);
		if (actionCache == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		String cacheKey = StringUtils.isNotBlank(actionCache.name()) ? actionCache.name() : target;

		request.setAttribute("_use_cache", true);
		request.setAttribute("_use_cache_key", cacheKey);
		request.setAttribute("_use_cache_content_type", actionCache.contentType());

		String renderContent = CacheKit.get("actionCache", cacheKey);
		if (renderContent != null) {
			response.setContentType(actionCache.contentType());

			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.write(renderContent);

				if (Jpress.isDevMode()) {
					String msg = "\r\n==================================================================\r\n";
					msg += "======================cached target:" + target + "===========================\r\n";
					msg += "==================================================================\r\n";
					log.error(msg);
				}
				isHandled[0] = true;
			} catch (Exception e) {
				RenderFactory.me().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} else {
			next.handle(target, request, response, isHandled);
		}
	}

}
