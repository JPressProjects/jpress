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
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.template.Template;
import io.jpress.template.TemplateUtils;
import io.jpress.utils.FileUtils;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin/template", viewPath = "/WEB-INF/admin/template")
@Before(ActionCacheClearInterceptor.class)
public class _TemplateController extends JBaseController {

	public void index() {
		List<Template> templateList = TemplateUtils.scanTemplates();
		setAttr("templateList", templateList);
		setAttr("currentTemplate", TemplateUtils.currentTemplate());
	}

	public void enable() {
		String id = getPara("id");

		if (!StringUtils.isNotBlank(id) ) {
			renderAjaxResultForError();
			return;
		}
		
		boolean isSuccess = TemplateUtils.templateChang(id);
		if(isSuccess){
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	public void install() {
		keepPara();

		if (isMultipartRequest()) {
			UploadFile ufile = getFile();
			String webRoot = PathKit.getWebRootPath();

			StringBuilder newFileName = new StringBuilder(webRoot).append("/templates/").append(ufile.getFileName());

			File newfile = new File(newFileName.toString());

			if (newfile.exists()) {
				renderAjaxResultForError("该模板已经安装！");
				return;
			}

			if (!newfile.getParentFile().exists()) {
				newfile.getParentFile().mkdirs();
			}

			ufile.getFile().renameTo(newfile);
			String zipPath = newfile.getAbsolutePath();

			try {
				FileUtils.unzip(zipPath);
			} catch (IOException e) {
				renderAjaxResultForError("模板文件解压缩失败！");
				return;
			}

			renderAjaxResultForSuccess();
		}
	}

	public void edit() {
		keepPara();
		String path = Jpress.currentTemplate().getPath();
		File pathFile = new File(PathKit.getWebRootPath(), path);

		File[] dirs = pathFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		setAttr("dirs", dirs);

		String dirName = getPara("d");
		if (dirName != null) {
			pathFile = new File(pathFile, dirName);
		}

		File[] files = pathFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isDirectory();
			}
		});
		setAttr("files", files);

		String fileName = getPara("f", "index.html");
		File editFile = null;
		if (fileName != null && files != null && files.length > 0) {
			for (File f : files) {
				if (fileName.equals(f.getName())) {
					editFile = f;
					break;
				}
			}
		}

		setAttr("f", fileName);
		if (editFile != null) {
			String fileContent = FileUtils.readString(editFile);
			if (fileContent != null) {
				fileContent = fileContent.replace("<", "&lt;").replace(">", "&gt;");
				setAttr("fileContent", fileContent);
				setAttr("editFile", editFile);
			}
		}

	}

	public void editsave() {
		String path = Jpress.currentTemplate().getPath();
		File pathFile = new File(PathKit.getWebRootPath(), path);

		String dirName = getPara("d");

		if (dirName != null) {
			pathFile = new File(pathFile, dirName);
		}

		String fileName = getPara("f");

		// 没有用getPara原因是，getPara因为安全问题会过滤某些html元素。
		String fileContent = getRequest().getParameter("fileContent");

		fileContent = fileContent.replace("&lt;", "<").replace("&gt;", ">");

		File file = new File(pathFile, fileName);
		FileUtils.writeString(file, fileContent);

		renderAjaxResultForSuccess();
	}

	public void menu() {
		List<Content> list = Content.DAO.findByModule("menu", "order_number ASC");
		ModelSorter.sort(list);
		setAttr("menus", list);

		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			Content c = Content.DAO.findById(id);
			setAttr("menu", c);
		}
	}

	@Before(UCodeInterceptor.class)
	public void menusave() {
		Content c = getModel(Content.class);
		c.setModule("menu");
		c.setModified(new Date());
		if (c.getCreated() == null) {
			c.setCreated(new Date());
		}
		c.saveOrUpdate();
		renderAjaxResultForSuccess();
	}

	@Before(UCodeInterceptor.class)
	public void menudel() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			if (Content.DAO.deleteById(id)) {
				renderAjaxResultForSuccess();
			}
		}
		renderAjaxResultForError();
	}

	public void setting() {

		keepPara();

		if (TemplateUtils.existsFile("tpl_setting.html")) {
			String include = TemplateUtils.getTemplatePath() + "/tpl_setting.html";
			setAttr("include", "../../.." + include);
		}
	}

	@Before(UCodeInterceptor.class)
	public void settingsave() {
		keepPara();
		renderAjaxResultForSuccess("成功！");
	}

}
