/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.core.template.TemplateUtil;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.functions.JPressCoreFunctions;
import io.jpress.web.render.TemplateRender;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/template", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _TemplateController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private MenuService ms;

    @Inject
    private OptionService optionService;


    @AdminMenu(text = "所有模板", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 0)
    public void list() {

        String title = getPara("title");
        List<Template> templates = TemplateManager.me().getInstalledTemplates();

        if (StrUtil.isNotBlank(title)) {
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

        setAttr("templateCount", templates == null ? 0 : templates.size());

        render("template/list.html");
    }

    public void doTriggerTemplateEnable() {
        Boolean trigger = !JPressOptions.isTemplatePreviewEnable();
        optionService.saveOrUpdate(JPressConsts.OPTION_WEB_TEMPLATE_PREVIEW_ENABLE, trigger.toString());
        JPressOptions.set(JPressConsts.OPTION_WEB_TEMPLATE_PREVIEW_ENABLE, trigger.toString());
        renderOkJson();
    }


    @AdminMenu(text = "安装", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 5)
    public void install() {
        render("template/install.html");
    }

    /**
     * 进行模板安装
     */
    public void doInstall() {

        render404If(!isMultipartRequest());

        //模板文件上传
        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("success", false));
            return;
        }


        //判断上传的 是否是 .zip 文件 目前只支持 .zip 文件
        if (!".zip".equalsIgnoreCase(FileUtil.getSuffix(uploadFile.getFileName()))) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "只支持 .zip 的压缩模板文件"));
            deleteFileQuietly(uploadFile.getFile());
            return;
        }

        //模板的短ID：md5(id).substring(0, 6)
        String templateShortId = TemplateUtil.readTemplateShortId(uploadFile.getFile());

        if (StrUtil.isBlank(templateShortId)) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "安装失败，您上传的可能不是 JPress 模板文件"));
            deleteFileQuietly(uploadFile.getFile());
            return;
        }


        String webRoot = PathKit.getWebRootPath();
        StringBuilder newFileName = new StringBuilder(webRoot);
        newFileName.append(File.separator);
        newFileName.append("templates");
        newFileName.append(File.separator);
        newFileName.append("dockers"); // 优先安装在docker的映射目录下

        File templateInstallPath = new File(newFileName.toString());
        if (!templateInstallPath.exists() || !templateInstallPath.isDirectory()) {
            templateInstallPath = templateInstallPath.getParentFile();
        }

        templateInstallPath = new File(templateInstallPath, templateShortId);


        //获取安装方式
        String type = getPara("type", "default");

        //如果为默认安装
        if ("default".equals(type)) {

            //判断模板是否存在 如果存在删除上传模板 不存在继续
            if (templateInstallPath.exists()) {
                renderJson(Ret.fail()
                        .set("success", false)
                        .set("message", "该模板可能已经存在，无法进行安装。"));
                deleteFileQuietly(uploadFile.getFile());

            } else {
                doInstall(uploadFile, templateInstallPath.getPath());
            }
        }

        //如果是覆盖安装
        else if ("cover".equals(type)) {
            doInstall(uploadFile, templateInstallPath.getPath());
        }

        //如果是全新安装
        else if ("new".equals(type)) {
            //判断模板是否存在 如果存在那么 删除原有模板
            if (templateInstallPath.exists()) {
                deleteFileQuietly(templateInstallPath);
            }

            doInstall(uploadFile, templateInstallPath.getPath());
        }


        if (getRender() == null) {
            renderJson(Ret.ok().set("success", true));
        }
    }


    private void doInstall(UploadFile uploadFile, String templatePath) {
        //模板文件解压
        try {
            TemplateUtil.unzip(uploadFile.getFile().getAbsolutePath(), templatePath, "UTF-8");
        }
        // UTF-8 编码错误，尝试使用 GBK 编码解压缩
        catch (IllegalArgumentException e) {
            try {
                TemplateUtil.unzip(uploadFile.getFile().getAbsolutePath(), templatePath, "GBK");
            } catch (Exception ex) {
                deleteFileQuietly(new File(templatePath));
                renderFailAndDeleteUnzipFiles(templatePath, e);
            }
        } catch (Exception e) {
            deleteFileQuietly(new File(templatePath));
            renderFailAndDeleteUnzipFiles(templatePath, e);
        } finally {
            //安装成功，无论是否成功，删除模板zip包
            deleteFileQuietly(uploadFile.getFile());
        }

    }

    private void renderFailAndDeleteUnzipFiles(String templatePath, Exception e) {
        deleteFileQuietly(new File(templatePath));
        LogKit.error(e.toString(), e);
        renderJson(Ret.fail()
                .set("success", false)
                .set("message", "模板文件解压缩失败"));
    }


    private void deleteFileQuietly(File file) {
        org.apache.commons.io.FileUtils.deleteQuietly(file);
    }


    public void doEnable() {
        String tid = getPara("tid");
        Template template = TemplateManager.me().getTemplateById(tid);

        if (template == null) {
            renderJson(Ret.fail().set("message", "没有该模板"));
            return;
        }

        JPressOptions.set("web_template", template.getId());
        optionService.saveOrUpdate("web_template", template.getId());

        TemplateManager.me().setCurrentTemplate(template.getId());
        TemplateManager.me().clearCache();

        renderOkJson();
    }


    public void doUninstall() {
        String tid = getPara("tid");
        Template template = TemplateManager.me().getTemplateById(tid);

        if (template == null) {
            renderJson(Ret.fail().set("message", "没有该模板"));
            return;
        }

        template.uninstall();
        renderOkJson();
    }


    @AdminMenu(text = "设置", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 88)
    public void setting() {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            render("template/setting.html");
            return;
        }
        setAttr("template", template);

        String view = template.matchView("setting_v4.html", false);
        if (view == null) {
            render("template/setting.html");
            return;
        }

        render(new TemplateRender(template.buildRelativePath(view), false));
    }

    @AdminMenu(text = "编辑", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 99)
    public void edit() {

        String dirName = getPara("d");
        //防止浏览非模板目录之外的其他目录
        render404If(dirName != null && dirName.contains(".."));
        setParentDirAttr(dirName);

        String editFileName = getPara("f", "index.html");
        render404If(editFileName.contains("/") || editFileName.contains(".."));


        render("template/edit.html");


        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return;
        }
        setAttr("template", template);

        File basePath = StrUtil.isNotBlank(dirName)
                ? new File(template.getAbsolutePathFile(), dirName)
                : template.getAbsolutePathFile();


        File[] files = basePath.listFiles((file) -> file.getName().endsWith(".html")
                || file.getName().endsWith(".css")
                || file.getName().endsWith(".js")
                || JPressCoreFunctions.isImage(file.getName())
                || file.isDirectory());

        List<String> srcFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory()) {
                srcFiles.add(file.getName());
            }
        }

        String absPath = template.getAbsolutePathFile().getAbsolutePath();

        setAttr("srcFiles", srcFiles);
        setAttr("prefixPath", absPath.substring(absPath.indexOf(File.separator.concat("templates"))));

        setAttr("files", buildFileInfos(files));
        setAttr("d", dirName);

        if (ArrayUtil.isNullOrEmpty(files)) {
            return;
        }


        File editFile = StrUtil.isBlank(editFileName) ? files[0] : getEditFile(editFileName, files);

        setAttr("f", editFile.getName());

        if (editFile.isFile()) {
            setAttr("editFileContent", StrUtil.escapeHtml(FileUtil.readString(editFile)));
        }

    }

    private void setParentDirAttr(String dirName) {
        if (StrUtil.isBlank(dirName)
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

    private List<FileInfo> buildFileInfos(File[] files) {
        List<FileInfo> fileInfoList = new ArrayList<>(files.length);
        for (File file : files) {
            fileInfoList.add(new FileInfo(file));
        }

        fileInfoList.sort((file1, file2) -> {
            if (file1.isDir() && file2.isDir()) {
                return file1.getName().compareTo(file2.getName());
            }

            if (file1.isDir() && !file2.isDir()) {
                return -1;
            }
            if (!file1.isDir() && file2.isDir()) {
                return 1;
            }

            if ("index.html".equals(file2.getName())) {
                return 1;
            }

            if (!file2.getName().endsWith(".html")) {
                return -1;
            }

            return file1.getName().compareTo(file2.getName());
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
        render404If(dirName != null && dirName.contains(".."));
        render404If(fileName.contains("/") || fileName.contains(".."));


        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            renderJson(Ret.fail().set("message", "当前模板无法编辑"));
            return;
        }


        File pathFile = template.getAbsolutePathFile();

        if (StrUtil.isNotBlank(dirName)) {
            pathFile = new File(pathFile, dirName);
        }


        String fileContent = getOriginalPara("fileContent");
        if (StrUtil.isBlank(fileContent)) {
            renderJson(Ret.fail().set("message", "不能存储空内容"));
            return;
        }

        File file = new File(pathFile, fileName);
        if (!file.canWrite()) {
            renderJson(Ret.fail().set("message", "当前文件没有写入权限"));
            return;
        }


        if (file.exists() && file.isDirectory()) {
            renderJson(Ret.fail().set("message", "存储失败，未指定任何文件"));
            return;
        }

        FileUtil.writeString(file, fileContent);

        TemplateManager.me().clearCache();

        renderOkJson();
    }


    @AdminMenu(text = "菜单", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 6)
    public void menu() {
        List<Menu> menus = ms.findListByType(Menu.TYPE_MAIN);
        SortKit.toLayer(menus);
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


    @EmptyValidate({
            @Form(name = "menu.text", message = "菜单名称不能为空"),
            @Form(name = "menu.url", message = "Url地址不能为空"),
    })
    public void doMenuSave() {
        Menu menu = getModel(Menu.class);
        ms.saveOrUpdate(menu);
        renderOkJson();
    }


    public void doMenuDel() {
        int id = getParaToInt(0, 0);
        render404If(id <= 0);

        List<Menu> childMenus = ms.findListByParentId(id);
        if (childMenus != null) {
            for (Menu menu : childMenus) {
                menu.setPid(0L);
                ms.update(menu);
            }
        }

        ms.deleteById(id);
        renderOkJson();
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

    public void doUploadFile() {

        UploadFile uploadFile = getFile();
        String fileName = uploadFile.getFileName();
        String dirName = getPara("d", "").trim();

        //防止浏览非模板目录之外的其他目录
        render404If(dirName.contains(".."));
        render404If(fileName.contains("/") || fileName.contains(".."));

        Template template = TemplateManager.me().getCurrentTemplate();
        render404If(template == null);

        File pathFile = new File(template.getAbsolutePathFile(), dirName);
        try {
            FileUtils.copyFile(uploadFile.getFile(), new File(pathFile, fileName));
        } catch (Exception e) {
            e.printStackTrace();
            renderFailJson();
            return;
        } finally {
            deleteFileQuietly(uploadFile.getFile());
        }

        if (fileName.toLowerCase().endsWith(".html")) {
            template.addNewHtml(fileName);
            TemplateManager.me().clearCache();
        }

        renderOkJson();
    }


    public void doDelFile() {
        String path = getPara("path");

        //防止删除非模板目录之外的其他目录文件
        render404If(path != null && path.contains(".."));

        Template template = TemplateManager.me().getCurrentTemplate();
        render404If(template == null);

        File delFile = new File(template.getAbsolutePathFile(), path);
        String delFileName = delFile.getName();
        if (delFile.isDirectory() || !delFile.delete()) {
            renderFailJson();
        } else {
            if (delFileName.toLowerCase().endsWith(".html")) {
                template.deleteHtml(delFileName);
                TemplateManager.me().clearCache();
            }
            renderOkJson();
        }
    }

}
