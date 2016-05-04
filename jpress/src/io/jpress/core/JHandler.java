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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;

import io.jpress.core.addon.HookInvoker;
import io.jpress.install.InstallUtils;
import io.jpress.model.Option;
import io.jpress.router.RouterKit;
import io.jpress.utils.FileUtils;

public class JHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		long time = System.currentTimeMillis();

		String cpath = request.getContextPath();

		request.setAttribute("CPATH", cpath);
		request.setAttribute("SPATH", cpath + "/static");
		request.setAttribute("URI", request.getRequestURI());
		request.setAttribute("URL", request.getRequestURL().toString());

		if (target.indexOf('.') != -1) {
			if (isDisableAccess(target)) {
				HandlerKit.renderError404(request, response, isHandled);
			}
			return;
		}

		// 检测是否安装
		if (!Jpress.isInstalled() && !target.startsWith("/install")) {
			HandlerKit.redirect(cpath + "/install", request, response, isHandled);
			return;
		}

		// 安装完成，但还没有加载完成...
		if (Jpress.isInstalled() && !Jpress.isLoaded()) {
			InstallUtils.renderInstallFinished(request, response, isHandled);
			return;
		}

		if (Jpress.isInstalled() && Jpress.isLoaded()) {
			setGlobalAttrs(request);
		}

		target = RouterKit.converte(target, request, response);
		target = HookInvoker.router_converte(target, request, response);

		next.handle(target, request, response, isHandled);

		if (Jpress.isDevMode()) {
			System.err.println("--->spend time:" + (System.currentTimeMillis() - time));
		}
	}

	private void setGlobalAttrs(HttpServletRequest request) {
		request.setAttribute("TPATH", request.getContextPath() + Jpress.currentTemplate().getPath());
		Boolean cdnEnable = Option.findValueAsBool("cdn_enable");
		if (cdnEnable != null && cdnEnable) {
			String cdnDomain = Option.findValue("cdn_domain");
			if (cdnDomain != null && !"".equals(cdnDomain.trim())) {
				request.setAttribute("CDN", cdnDomain);
			}
		}

		request.setAttribute("WEB_NAME", Option.findValue("web_name"));
		request.setAttribute("WEB_TITLE", Option.findValue("web_title"));
		request.setAttribute("META_KEYWORDS", Option.findValue("meta_keywords"));
		request.setAttribute("META_DESCRIPTION", Option.findValue("meta_description"));

	}

	private static boolean isDisableAccess(String target) {
		// 防止直接访问模板文件
		if (target.startsWith("/templates") && target.endsWith(".html")) {
			return true;
		}
		// 防止直接访问jsp文件页面
		if (".jsp".equalsIgnoreCase(FileUtils.getSuffix(target))) {
			return true;
		}

		return false;
	}

}
