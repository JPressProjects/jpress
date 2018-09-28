package io.jpress.web.admin;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.service.MenuService;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.kits.MenuKits;
import org.apache.commons.lang.StringEscapeUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

        String title = getPara("title");
        List<Template> templates = TemplateManager.me().getInstalledTemplates();

        if (StrUtils.isNotBlank(title)) {
            List<Template> searchTemplate = new ArrayList<>();
            for (Template template : templates) {
                if (template.getTitle() != null &&
                        template.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    searchTemplate.add(template);
                }
            }

            setAttr("templates", searchTemplate);
        } else {
            setAttr("templates", templates);
        }

        render("template/list.html");
    }


    @AdminMenu(text = "安装", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 5)
    public void install() {
        render("template/install.html");
    }

    /**
     * 进行模板安装
     */
    public void doInstall() {

        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile ufile = getFile();
        if (ufile == null) {
            renderJson(Ret.fail().set("success", false));
            return;
        }

        if (!".zip".equals(FileUtils.getSuffix(ufile.getFileName()))) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "只支持 .zip 的压缩模板文件"));
            return;
        }

        String webRoot = PathKit.getWebRootPath();
        StringBuilder newFileName = new StringBuilder(webRoot);
        newFileName.append(File.separator);
        newFileName.append("templates");
        newFileName.append(File.separator);
        newFileName.append(ufile.getFileName());

        File newfile = new File(newFileName.toString());
        if (newfile.exists()) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "该模板已经安装"));
            return;
        }

        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }

        ufile.getFile().renameTo(newfile);
        String zipPath = newfile.getAbsolutePath();

        try {
            FileUtils.unzip(zipPath, newfile.getParentFile().getAbsolutePath());
        } catch (IOException e) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "模板文件解压缩失败"));
            return;
        }

        renderJson(Ret.ok().set("success", true));

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
    public void setting() {
        Template template = TemplateManager.me().getCurrentTemplate();
        setAttr("template", template);

        String view = template.matchTemplateFile("setting.html");
        if (view == null) {
            render("template/setting.html");
            return;
        }

        render(template.getWebAbsolutePath() + "/setting.html");
    }

    @AdminMenu(text = "编辑", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 99)
    public void edit() {

        String dirName = getPara("d");
        //防止浏览非模板目录之外的其他目录
        if (dirName != null && dirName.contains("..")) {
            renderError(404);
            return;
        } else {
            setParentDirAttr(dirName);
        }

        String editFileName = getPara("f", "index.html");
        if (editFileName.contains("/") || editFileName.contains("..")) {
            renderError(404);
            return;
        }


        render("template/edit.html");


        Template template = TemplateManager.me().getCurrentTemplate();
        setAttr("template", template);

        File basePath = StrUtils.isNotBlank(dirName)
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


        File editFile = StrUtils.isBlank(editFileName) ? files[0] : getEditFile(editFileName, files);

        setAttr("d", dirName);
        setAttr("f", editFile.getName());
        setAttr("editFileContent", StringEscapeUtils.escapeHtml(FileUtils.readString(editFile)));


    }

    private void setParentDirAttr(String dirName) {
        if (StrUtils.isBlank(dirName)
                || "/".equals(dirName)
                || "./".equals(dirName)) {
            return;
        }

        if (!dirName.contains("/")) {
            setAttr("parentDir", "");
        } else {
            if (dirName.endsWith("/")) {
                dirName = dirName.substring(0, dirName.lastIndexOf("/"));
            }
            setAttr("parentDir", dirName.substring(0, dirName.lastIndexOf("/")));
        }
    }

    private List<FileInfo> doGetFileInfos(File[] files) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        for (File file : files) {
            fileInfoList.add(new FileInfo(file));
        }

        fileInfoList.sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {

                if (o1.isDir() && !o2.isDir())
                    return -1;
                if (!o1.isDir() && o2.isDir())
                    return 1;

                if (o2.getName().equals("index.html")) {
                    return 1;
                }

                return o2.getName().compareTo(o1.getName());
            }
        });

        return fileInfoList;
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

        String dirName = getPara("d");
        String fileName = getPara("f");

        //防止浏览非模板目录之外的其他目录
        if (dirName != null && dirName.contains("..")) {
            renderError(404);
            return;
        }

        if (fileName.contains("/") || fileName.contains("..")) {
            renderError(404);
            return;
        }


        File pathFile = new File(TemplateManager.me().getCurrentTemplate().getAbsolutePath());

        if (StrUtils.isNotBlank(dirName)) {
            pathFile = new File(pathFile, dirName);
        }


        String fileContent = getPara("fileContent");
        if (StrUtils.isBlank(fileContent)) {
            renderJson(Ret.fail().set("message", "不能存储空内容"));
            return;
        }

        File file = new File(pathFile, fileName);
        FileUtils.writeString(file, fileContent);

        renderJson(Ret.ok());
    }


    @AdminMenu(text = "菜单", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 6)
    public void menu() {
        List<io.jpress.model.Menu> menus = ms.findListByType(io.jpress.model.Menu.TYPE_MAIN);
        MenuKits.toLayerCategories(menus);
        setAttr("menus", menus);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (io.jpress.model.Menu menu : menus) {
                if (menu.getId() == id) {
                    setAttr("menu", menu);
                }
            }
        }

        render("template/menu.html");
    }

    public void doMenuSave() {
        io.jpress.model.Menu category = getModel(io.jpress.model.Menu.class);
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
