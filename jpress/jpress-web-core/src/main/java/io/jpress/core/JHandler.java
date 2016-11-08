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

import io.jpress.Consts;
import io.jpress.install.InstallUtils;
import io.jpress.model.query.OptionQuery;
import io.jpress.router.RouterManager;
import io.jpress.template.TemplateManager;
import io.jpress.ui.freemarker.tag.MenusTag;
import io.jpress.utils.FileUtils;

public class JHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.startsWith("/websocket")) {
			return;
		}
		
		String CPATH = request.getContextPath();
		request.setAttribute("REQUEST", request);
		request.setAttribute("CPATH", CPATH);
		request.setAttribute("SPATH", CPATH + "/static");
		request.setAttribute("JPRESS_VERSION", Jpress.VERSION);

		// 程序还没有安装
		if (!Jpress.isInstalled()) {
			if (target.indexOf('.') != -1) {
				return;
			}

			if (!target.startsWith("/install")) {
				processNotInstall(request, response, isHandled);
				return;
			}
		}

		// 安装完成，但还没有加载完成...
		if (Jpress.isInstalled() && !Jpress.isLoaded()) {
			if (target.indexOf('.') != -1) {
				return;
			}

			InstallUtils.renderInstallFinished(request, response, isHandled);
			return;
		}

		if (Jpress.isInstalled() && Jpress.isLoaded()) {
			setGlobalAttrs(request);
		}

		if (isDisableAccess(target)) {
			HandlerKit.renderError404(request, response, isHandled);
		}

		String originalTarget = target;
		target = RouterManager.converte(target, request, response);

		if (!originalTarget.equals(target)) {
			request.setAttribute("_original_target", originalTarget);
		}

		next.handle(target, request, response, isHandled);

	}

	private void processNotInstall(HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		String CPATH = request.getContextPath();
		HandlerKit.redirect(CPATH + "/install", request, response, isHandled);
	}

	private static boolean isDisableAccess(String target) {
		// 防止直接访问模板文件
		if (target.endsWith(".html") && target.startsWith("/templates")) {
			return true;
		}
		// 防止直接访问jsp文件页面
		if (".jsp".equalsIgnoreCase(FileUtils.getSuffix(target))) {
			return true;
		}

		return false;
	}

	private void setGlobalAttrs(HttpServletRequest request) {

		request.setAttribute(MenusTag.TAG_NAME, new MenusTag(request));

		if (null != TemplateManager.me().currentTemplate()) {
			request.setAttribute("TPATH", TemplateManager.me().currentTemplate().getPath());
			request.setAttribute("CTPATH", request.getContextPath() + TemplateManager.me().currentTemplate().getPath());
		} else {
			request.setAttribute("TPATH", "");
			request.setAttribute("CTPATH", request.getContextPath());
		}

		Boolean cdnEnable = OptionQuery.me().findValueAsBool("cdn_enable");
		if (cdnEnable != null && cdnEnable == true) {
			String cdnDomain = OptionQuery.me().findValue("cdn_domain");
			if (cdnDomain != null && !"".equals(cdnDomain.trim())) {
				request.setAttribute("CDN", cdnDomain);
			}
		}

		request.setAttribute(Consts.ATTR_GLOBAL_WEB_NAME, OptionQuery.me().findValue("web_name"));
		request.setAttribute(Consts.ATTR_GLOBAL_WEB_TITLE, OptionQuery.me().findValue("web_title"));
		request.setAttribute(Consts.ATTR_GLOBAL_WEB_SUBTITLE, OptionQuery.me().findValue("web_subtitle"));
		request.setAttribute(Consts.ATTR_GLOBAL_META_KEYWORDS, OptionQuery.me().findValue("meta_keywords"));
		request.setAttribute(Consts.ATTR_GLOBAL_META_DESCRIPTION, OptionQuery.me().findValue("meta_description"));
	}

}
