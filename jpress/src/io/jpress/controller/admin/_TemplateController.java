package io.jpress.controller.admin;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

@UrlMapping(url = "/admin/template", viewPath = "/WEB-INF/admin/template")
@Before(AdminInterceptor.class)
public class _TemplateController extends JBaseController {

	public void index() {
		keepPara();
		List<Template> themeList = scanTemplates();
		setAttr("templateList", themeList);
		
		System.out.println(Jpress.currentTemplate().getPath());
		System.out.println(Jpress.currentTemplate().getWidgetContainers());
	}

	public void install() {
		keepPara();

		if (isMultipartRequest()) {
			UploadFile ufile = getFile();
			String webRoot = PathKit.getWebRootPath();
			
			StringBuilder newFileName = new StringBuilder(webRoot)
			.append("/templates/").append(ufile.getFileName());

			File newfile = new File(newFileName.toString());
			
			if(newfile.exists()){
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
	}

	public void menu() {
		keepPara();
		List<Content> list = Content.DAO.findMenuList();
		ModelSorter.sort(list);
		setAttr("menus", list);
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
		renderAjaxResultForSuccess("菜单保存成功！");
	}

	public void setting() {
		keepPara();

		if (TemplateUtils.existsFile("template_setting.html")) {
			String include = "../../../templates/%s/template_setting.html";
			setAttr("include",
					String.format(include, TemplateUtils.getTemplateName()));
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
