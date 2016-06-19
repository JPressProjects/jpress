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
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import io.jpress.Consts;
import io.jpress.core.JBaseController;
import io.jpress.core.Jpress;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.FileUtils;
import io.jpress.utils.StringUtils;
import io.jpress.utils.WordPressUtils;

@RouterMapping(url = "/admin/tools", viewPath = "/WEB-INF/admin/tools")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ToolsController extends JBaseController {

	public void index() {

	}

	public void druid() {

	}

	public void _import() {

	}

	public void export() {

	}

	public void wechatImport() {

	}

	public void wordpressImport() {
		keepPara();

		if (!isMultipartRequest()) {
			setAttr("modules", Jpress.currentTemplate().getModules());
			return;
		}

		UploadFile ufile = getFile();
		if (ufile == null) {
			renderAjaxResultForError("您还未选择WordPress文件");
			return;
		}

		if (!".xml".equals(FileUtils.getSuffix(ufile.getFileName()))) {
			renderAjaxResultForError("请选择从WordPress导出的XML文件");
			return;
		}

		String newPath = AttachmentUtils.moveFile(ufile);
		File xmlFile = new File(PathKit.getWebRootPath(), newPath);

		List<Content> contents = WordPressUtils.parse(xmlFile);
		if (contents == null || contents.size() == 0) {
			renderAjaxResultForError("无法解析WordPress格式，可能是导出有误");
			return;
		}

		String moduelName = getPara("_module");
		if (!StringUtils.isNotBlank(moduelName)) {
			renderAjaxResultForError("请选择导入目标");
			return;
		}

		for (Content c : contents) {
			if (c.getCreated() == null)
				c.setCreated(new Date());

			String slug = c.getSlug();
			if (!StringUtils.isNotBlank(slug)) {
				slug = c.getTitle();
			}

			if (slug != null) {
				slug = slug.replaceAll("(\\s+)|(\\.+)|(。+)|(…+)|[\\$,，？\\-?、；;:!]", "_");
				slug = slug.replaceAll("(?!_)\\pP|\\pS", "");

				c.setSlug(slug);
			}

			if (c.getUserId() == null) {
				User user = getAttr(Consts.ATTR_USER);
				c.setUserId(user.getId());
			}

			if (c.getModule() == null) {
				c.setModule(moduelName);
			}

			c.save();
		}

		renderAjaxResultForSuccess();
	}

}
