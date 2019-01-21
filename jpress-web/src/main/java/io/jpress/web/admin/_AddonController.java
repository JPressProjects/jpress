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

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;
import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/addon")
public class _AddonController extends AdminControllerBase {


    @AdminMenu(text = "所有插件", groupId = JPressConsts.SYSTEM_MENU_ADDON, order = 0)
    public void index() {



        render("addon/list.html");
    }


    @AdminMenu(text = "安装", groupId = JPressConsts.SYSTEM_MENU_ADDON, order = 5)
    public void install() {
        render("addon/install.html");
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

        if (!".zip".equals(FileUtil.getSuffix(ufile.getFileName()))) {
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
        newFileName.append(ufile.getOriginalFileName());


        String templatePath = newFileName.substring(0, newFileName.length() - 4);
        if (new File(templatePath).exists()) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "该模板已经安装"));
            deleteFileQuietly(ufile.getFile());
            return;
        }

        File templateZipFile = new File(newFileName.toString());
        if (!templateZipFile.getParentFile().exists()) {
            templateZipFile.getParentFile().mkdirs();
        }

        try {
            org.apache.commons.io.FileUtils.moveFile(ufile.getFile(), templateZipFile);
            FileUtil.unzip(templateZipFile.getAbsolutePath(),
                    templateZipFile.getParentFile().getAbsolutePath());
        } catch (IOException e) {
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

        renderJson(Ret.ok());
    }


    public void doUninstall() {
        String tid = getPara("tid");
        Template template = TemplateManager.me().getTemplateById(tid);

        if (template == null) {
            renderJson(Ret.fail().set("message", "没有该模板"));
            return;
        }

        template.uninstall();
        renderJson(Ret.ok());
    }








}
