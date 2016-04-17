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
package io.jpress.controller.admin;

import java.io.File;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.core.addon.AddonManager;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;

@UrlMapping(url = "/admin/addon", viewPath = "/WEB-INF/admin/addon")
@Before(AdminInterceptor.class)
public class _AddonController extends JBaseController {

	public void index() {
		keepPara();
		setAttr("addons", AddonManager.get().getAddons());
	}

	public void install() {
		keepPara();
		if (isMultipartRequest()) {
			UploadFile ufile = getFile();
			String webRoot = PathKit.getWebRootPath();

			StringBuilder newFileName = new StringBuilder(webRoot).append("/WEB-INFO/addons/")
					.append(ufile.getFileName());

			File newfile = new File(newFileName.toString());

			if (newfile.exists()) {
				renderAjaxResultForError("该插件已经安装！");
				return;
			}

			if (!newfile.getParentFile().exists()) {
				newfile.getParentFile().mkdirs();
			}

			ufile.getFile().renameTo(newfile);

			renderAjaxResultForSuccess();
		}
	}

	public void uninstall() {
		keepPara();

		// AddonManager.get().stop(addon);
	}

	public void start() {
		keepPara();
	}

	public void stop() {
		keepPara();
	}

}
