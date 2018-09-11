package io.jpress.web.admin;

import com.jfinal.kit.Ret;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.kits.MenuKits;
import org.apache.commons.lang.StringEscapeUtils;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/template")
public class _TemplateController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private MenuService ms;

    @Inject
    private OptionService optionService;

    @AdminMenu(text = "所有模板", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 0)
    public void index() {
        List<Template> templates = TemplateManager.me().getInstalledTemplates();
        setAttr("templates", templates);

        render("template/list.html");
    }


    @AdminMenu(text = "安装", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 5)
    public void install() {
        render("template/install.html");
    }


    public void enable() {
        String tid = getPara("tid");
        Template template = TemplateManager.me().getTemplateById(tid);

        if (template == null) {
            renderJson(Ret.fail().set("message", "没有该模板"));
            return;
        }

        optionService.saveOrUpdate("web_template", template.getId());
        TemplateManager.me().setCurrentTemplate(template);

        renderJson(Ret.ok());
    }


    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 88)
    public void me() {
        render("user/me.html");
    }

    @AdminMenu(text = "编辑", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 99)
    public void edit() {

        render("template/edit.html");

        String dirName = getPara("d");
        String editFileName = getPara("f", "index.html");


        Template template = TemplateManager.me().getCurrentTemplate();
        setAttr("template", template);

        File basePath = StringUtils.isNotBlank(dirName)
                ? new File(template.getAbsolutePath(), dirName)
                : new File(template.getAbsolutePath());


        File[] files = basePath.listFiles((file) -> file.getName().endsWith(".html")
                || file.getName().endsWith(".css")
                || file.getName().endsWith(".js")
                || file.isDirectory());

        setAttr("files", doGetFileInfos(files));

        if (ArrayUtils.isNullOrEmpty(files)) {
            return;
        }


        File editFile = StringUtils.isBlank(editFileName) ? files[0] : getEditFile(editFileName, files);

        setAttr("d", dirName);
        setAttr("f", editFile.getName());

        setAttr("editFileContent", StringEscapeUtils.escapeHtml(FileUtils.readString(editFile)));
    }

    private FileInfo[] doGetFileInfos(File[] files) {
        FileInfo[] fileInfos = new FileInfo[files.length];
        for (int i = 0; i < fileInfos.length; i++) {
            fileInfos[i] = new FileInfo(files[i]);
        }
        return fileInfos;
    }

    private File getEditFile(String editFileName, File[] files) {
        for (File f : files) {
            if (editFileName.equals(f.getName())) {
                return f;
            }
        }
        return files[0];
    }


    public void doEditSave() {
        File pathFile = new File(TemplateManager.me().getCurrentTemplate().getAbsolutePath());

        String dirName = getPara("d");

        if (StringUtils.isNotBlank(dirName)) {
            pathFile = new File(pathFile, dirName);
        }

        String fileName = getPara("f");

        // 没有用getPara原因是，getPara因为安全问题会过滤某些html元素。
        String fileContent = getRequest().getParameter("fileContent");

        fileContent = StringEscapeUtils.unescapeHtml(fileContent);

        File file = new File(pathFile, fileName);
        FileUtils.writeString(file, fileContent);

        renderJson(Ret.ok());
    }


    @AdminMenu(text = "菜单", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 6)
    public void menu() {
        List<Menu> menus = ms.findListByType(Menu.TYPE_MAIN);
        MenuKits.toLayerCategories(menus);
        setAttr("menus", menus);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (Menu menu : menus) {
                if (menu.getId() == id) {
                    setAttr("menu", menu);
                }
            }
        }

        render("template/menu.html");
    }

    public void doMenuSave() {
        Menu category = getModel(Menu.class);
        ms.saveOrUpdate(category);
        redirect("/admin/template/menu");
    }

    public void doMenuDel() {
        int id = getParaToInt(0, 0);
        if (id == 0) {
            renderError(404);
            return;
        }

        ms.deleteById(id);
        renderJson(Ret.ok());
    }

    public static class FileInfo {
        private File file;

        public FileInfo(File file) {
            this.file = file;
        }

        public String getName() {
            return file.getName();
        }

        public boolean isDir() {
            return file.isDirectory();
        }
    }


}
