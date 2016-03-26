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

import io.jpress.model.Option;
import io.jpress.plugin.target.TargetKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;

public class JHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,HttpServletResponse response, boolean[] isHandled) {

		long time = System.currentTimeMillis();

		String contextPath = request.getContextPath();
		request.setAttribute("CPATH", contextPath);
		request.setAttribute("SPATH", contextPath + "/static");

		if (target.indexOf('.') != -1) {
			// 防止直接访问模板文件
			if (target.startsWith("/templates") && target.endsWith("html")) {
				HandlerKit.renderError404(request, response, isHandled);
			}
			
			if("sitemap.xml".equalsIgnoreCase(target)){
				target = "sitemap";
			}
			
			return;
		}
		
		// 检测是否安装
		if (!Jpress.isInstalled() && !target.startsWith("/install")) {
			HandlerKit.redirect(contextPath + "/install", request, response,isHandled);
			return;
		}

		if(Jpress.isInstalled()){
			request.setAttribute("TPATH",contextPath + "/templates/" + Option.findTemplateName());
			Boolean cdnEnable = Option.findValueAsBool("cdn_enable");
			if(cdnEnable != null && cdnEnable){
				String cdnDomain = Option.cacheValue("cdn_domain");
				if(cdnDomain!=null && !"".equals(cdnDomain.trim())){
					request.setAttribute("CDN", cdnDomain);
				}
			}
		}
		
		target = targetConvert(target, request, response);
		next.handle(target, request, response, isHandled);

		if (Jpress.isDevMode()) {
			System.err.println("--->time:" + (System.currentTimeMillis() - time));
		}

	}

	private String targetConvert(String target, HttpServletRequest request,HttpServletResponse response) {
		return TargetKit.converte(target, request, response);
	}

}
