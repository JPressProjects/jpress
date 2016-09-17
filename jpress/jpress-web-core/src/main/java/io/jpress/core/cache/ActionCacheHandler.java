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
package io.jpress.core.cache;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.render.RenderFactory;

import io.jpress.utils.StringUtils;

public class ActionCacheHandler extends Handler {

	static String[] urlPara = { null };
	static Log log = Log.getLog(ActionCacheHandler.class);

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		if (ActionCacheManager.isCloseActionCache()) {
			next.handle(target, request, response, isHandled);
			return;
		}

		Action action = JFinal.me().getAction(target, urlPara);
		if (action == null) {
			next.handle(target, request, response, isHandled);
			return;
		}

		ActionCache actionCache = action.getMethod().getAnnotation(ActionCache.class);
		if (actionCache == null) {
			actionCache = action.getControllerClass().getAnnotation(ActionCache.class);
			if (actionCache == null) {
				next.handle(target, request, response, isHandled);
				return;
			}
		}

		String originalTarget = (String) request.getAttribute("_original_target");
		String cacheKey = StringUtils.isNotBlank(originalTarget) ? originalTarget : target;

		String queryString = request.getQueryString();
		if (queryString != null) {
			queryString = "?" + queryString;
			cacheKey += queryString;
		}

		ActionCacheManager.enableCache(request);
		ActionCacheManager.setCacheKey(request, cacheKey);
		ActionCacheManager.setCacheContentType(request, actionCache.contentType());

		String renderContent = ActionCacheManager.getCache(request, cacheKey);
		if (renderContent != null) {
			response.setContentType(actionCache.contentType());

			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.write(renderContent);
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
