/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.render.RenderManager;
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
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.JPressShareFunctions;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
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
    public void index() {

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


    @AdminMenu(text = "安装", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 5)
    public void install() {
        render("template/install.html");
    }

    /**
     * 进行模板安装
     */
    public void doInstall() {

        render404If(!isMultipartRequest());

        UploadFile ufile = getFile();
        if (ufile == null) {
            renderJson(Ret.fail().set("success", false));
            return;
        }

        if (!".zip".equalsIgnoreCase(FileUtil.getSuffix(ufile.getFileName()))) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "只支持 .zip 的压缩模板文件"));
            deleteFileQuietly(ufile.getFile());
            return;
        }


        String webRoot = PathKit.getWebRootPath();
        StringBuilder newFileName = new StringBuilder(webRoot);
        newFileName.append(File.separator);
        newFileName.append("templates");
        newFileName.append(File.separator);
        newFileName.append("dockers"); // 优先安装在docker的映射目录下

        File templateRootPath = new File(newFileName.toString());
        if (!templateRootPath.exists() || !templateRootPath.isDirectory()) {
            templateRootPath = templateRootPath.getParentFile();
        }

        File templateZipFile = new File(templateRootPath, ufile.getOriginalFileName());
        String templatePath = templateZipFile.getAbsolutePath()
                .substring(0, templateZipFile.getAbsolutePath().length() - 4);

        if (new File(templatePath).exists()) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "该模板可能已经存在，无法进行安装。"));
            deleteFileQuietly(ufile.getFile());
            return;
        }


        try {
            FileUtils.moveFile(ufile.getFile(), templateZipFile);
            FileUtil.unzip(templateZipFile.getAbsolutePath(), templatePath);
        } catch (Exception e) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "模板文件解压缩失败"));
            return;
        } finally {
            //安装成功后，删除zip包
            deleteFileQuietly(templateZipFile);
            deleteFileQuietly(ufile.getFile());
        }

        renderJson(Ret.ok().set("success", true));
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
        TemplateManager.me().setCurrentTemplate(template);
        RenderManager.me().getEngine().removeAllTemplateCache();

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

        String view = template.matchTemplateFile("setting.html", false);
        if (view == null) {
            render("template/setting.html");
            return;
        }

        render(template.getRelativePath() + "/setting.html");
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
                || JPressShareFunctions.isImage(file.getName())
                || file.isDirectory());

        List srcFiles = new ArrayList<String>();
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
        setAttr("editFileContent", StrUtil.escapeHtml(FileUtil.readString(editFile)));

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

        fileInfoList.sort((o1, o2) -> {
            if (o1.isDir() && o2.isDir()){
                return o1.getName().compareTo(o2.getName());
            }

            if (o1.isDir() && !o2.isDir()) {
                return -1;
            }
            if (!o1.isDir() && o2.isDir()) {
                return 1;
            }

            if (o2.getName().equals("index.html")) {
                return 1;
            }

            if (!o2.getName().endsWith(".html")){
                return -1;
            }

            return o1.getName().compareTo(o2.getName());
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


        String fileContent = getPara("fileContent");
        if (StrUtil.isBlank(fileContent)) {
            renderJson(Ret.fail().set("message", "不能存储空内容"));
            return;
        }

        File file = new File(pathFile, fileName);
        if (!file.canWrite()) {
            renderJson(Ret.fail().set("message", "当前文件没有写入权限"));
            return;
        }

        FileUtil.writeString(file, fileContent);
        RenderManager.me().getEngine().removeAllTemplateCache();

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
                menu.setPid(0l);
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
        String dirName = getPara("d").trim();

        //防止浏览非模板目录之外的其他目录
        render404If(dirName != null && dirName.contains(".."));
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
            RenderManager.me().getEngine().removeAllTemplateCache();
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
        if (delFile.isDirectory() || delFile.delete() == false) {
            renderFailJson();
        } else {
            if (delFileName.toLowerCase().endsWith(".html")) {
                template.deleteHtml(delFileName);
                RenderManager.me().getEngine().removeAllTemplateCache();
            }
            renderOkJson();
        }
    }

}
