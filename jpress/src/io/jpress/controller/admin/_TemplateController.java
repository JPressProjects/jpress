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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.template.ConfigParser;
import io.jpress.template.Template;
import io.jpress.template.TemplateUtils;
import io.jpress.utils.FileUtils;

@UrlMapping(url = "/admin/template", viewPath = "/WEB-INF/admin/template")
@Before(AdminInterceptor.class)
public class _TemplateController extends JBaseController {

	public void index() {
		keepPara();
		List<Template> themeList = scanTemplates();
		setAttr("templateList", themeList);
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

		String fileName = getPara("f","index.html");
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
		
		//没有用getPara原因是，getPara因为安全问题会过滤某些html元素。
		String fileContent = getRequest().getParameter("fileContent");
		
		fileContent = fileContent.replace("&lt;","<").replace("&gt;",">");

		File file = new File(pathFile, fileName);
		FileUtils.writeString(file, fileContent);

		renderAjaxResultForSuccess();
	}

	public void menu() {
		keepPara();
		List<Content> list = Content.DAO.findMenuList();
		ModelSorter.sort(list);
		setAttr("menus", list);

		long id = getParaToLong("id", (long) 0);
		if (id > 0) {
			Content c = Content.DAO.findById(id);
			setAttr("menu", c);
		}
	}

	@Before(UCodeInterceptor.class)
	public void menusave() {
		Content c = getModel(Content.class);
		c.setModule("menu");
		c.setModified(new Date());
		if (c.getId() == null || c.getId() == 0) {
			c.setCreated(new Date());
		}
		c.saveOrUpdate();
		renderAjaxResultForSuccess();
	}

	@Before(UCodeInterceptor.class)
	public void menudel() {
		long id = getParaToLong("id", (long) 0);
		if (id > 0) {
			if (Content.DAO.deleteById(id)) {
				renderAjaxResultForSuccess();
			}
		}
		renderAjaxResultForError();
	}

	public void setting() {
		keepPara();

		if (TemplateUtils.existsFile("template_setting.html")) {
			String include = "../../..%s/template_setting.html";
			setAttr("include", String.format(include, TemplateUtils.getTemplatePath()));
		}
	}

	@Before(UCodeInterceptor.class)
	public void settingsave() {
		keepPara();
		renderAjaxResultForSuccess("成功！");
	}

	public void widget() {
		keepPara();
	}

	private List<Template> scanTemplates() {
		String basePath = PathKit.getWebRootPath() + "/templates";

		List<File> themeFolderList = new ArrayList<File>();
		scanThemeFloders(new File(basePath), themeFolderList);

		List<Template> templatelist = null;
		if (themeFolderList.size() > 0) {
			templatelist = new ArrayList<Template>();
			for (File themeFile : themeFolderList) {
				templatelist.add(new ConfigParser().parser(themeFile.getName()));
			}
		}

		return templatelist;
	}

	private void scanThemeFloders(File file, List<File> fillToList) {
		if (file.isDirectory()) {

			File configFile = new File(file, "config.xml");

			if (configFile.exists() && configFile.isFile()) {
				fillToList.add(file);
			} else {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File f : files) {
						if (f.isDirectory())
							scanThemeFloders(f, fillToList);
					}
				}
			}
		}
	}
}
