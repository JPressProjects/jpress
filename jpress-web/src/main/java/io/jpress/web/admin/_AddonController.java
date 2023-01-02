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

import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.MarkdownUtils;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonManager;
import io.jpress.core.addon.AddonUtil;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/addon", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AddonController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AddonController.class);


    @AdminMenu(text = "所有插件", groupId = JPressConsts.SYSTEM_MENU_ADDON, order = 0)
    public void list() {

        List<AddonInfo> addons = AddonManager.me().getAllAddonInfos();
        setAttr("addons", addons);
        render("addon/list.html");
    }


    @AdminMenu(text = "安装", groupId = JPressConsts.SYSTEM_MENU_ADDON, order = 5)
    public void install() {
        render("addon/install.html");
    }

    /**
     * 进行插件安装
     */
    public void doUploadAndInstall() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile ufile = getFile();
        if (ufile == null) {
            renderJson(Ret.fail().set("success", false));
            return;
        }

        if (!StringUtils.equalsAnyIgnoreCase(FileUtil.getSuffix(ufile.getFileName()), ".zip", ".jar")) {
            renderFailAndDeleteFile("只支持 .zip 或 .jar 的插件文件",ufile);
            return;
        }

        AddonInfo addon = AddonUtil.readSimpleAddonInfo(ufile.getFile());
        if (addon == null || StrUtil.isBlank(addon.getId())) {
            renderFailAndDeleteFile("无法读取插件配置信息。",ufile);
            return;
        }

        File newAddonFile = addon.buildJarFile();

        //当插件文件存在的时候，有两种可能
        // 1、该插件确实存在，此时不能再次安装
        // 2、该插件可能没有被卸载干净，此时需要尝试清除之前已经被卸载的插件
        if (newAddonFile.exists()) {

            //说明该插件已经被安装了
            if (AddonManager.me().getAddonInfo(addon.getId()) != null) {
                renderFailAndDeleteFile("该插件已经存在。",ufile);
                return;
            }
            //该插件之前已经被卸载了
            else {

                //尝试再次去清除jar包，若还是无法删除，则无法安装
                if (!AddonUtil.forceDelete(newAddonFile)) {
                    renderFailAndDeleteFile("该插件已经存在。",ufile);
                    return;
                }
            }
        }

        if (!newAddonFile.getParentFile().exists()) {
            newAddonFile.getParentFile().mkdirs();
        }

        try {
            FileUtils.moveFile(ufile.getFile(), newAddonFile);
            if (!AddonManager.me().install(newAddonFile)) {
                renderFailAndDeleteFile("该插件安装失败，请联系管理员。",ufile);
                return;
            }
            if (!AddonManager.me().start(addon.getId())) {
                renderFailAndDeleteFile("该插件安装失败，请联系管理员。",ufile);
                return;
            }
        } catch (Throwable e) {
            LOG.error("addon install error : ", e);
            renderFailAndDeleteFile("该插件安装失败，请联系管理员。",ufile);
            deleteFileQuietly(newAddonFile);
            return;
        }

        renderJson(Ret.ok().set("success", true));
    }


    public void upgrade() {

        String id = getPara("id");

        if (StrUtil.isBlank(id)) {
            renderError(404);
            return;
        }

        AddonInfo addonInfo = AddonManager.me().getAddonInfo(id);
        if (addonInfo == null) {
            renderError(404);
            return;
        }

        setAttr("addon", addonInfo);
        render("addon/upgrade.html");
    }

    /**
     * 进行插件安装
     */
    public void doUploadAndUpgrade() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile ufile = getFile();
        if (ufile == null) {
            renderFailAndDeleteFile(null);
            return;
        }

        String oldAddonId = getPara("id");
        AddonInfo oldAddon = AddonManager.me().getAddonInfo(oldAddonId);
        if (oldAddon == null) {
            renderFailAndDeleteFile("无法读取旧的插件信息，可能已经被卸载。", ufile);
            return;
        }

        if (!StringUtils.equalsAnyIgnoreCase(FileUtil.getSuffix(ufile.getFileName()), ".zip", ".jar")) {
            renderFailAndDeleteFile("只支持 .zip 或 .jar 的插件文件", ufile);
            return;
        }

        try {
            Ret ret = AddonManager.me().upgrade(ufile.getFile(), oldAddonId);
            render(ret);
            return;
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            renderFailAndDeleteFile("插件升级失败，请联系管理员", ufile);
            return;
        } finally {
            deleteFileQuietly(ufile.getFile());
        }
    }

    private void deleteFileQuietly(File file) {
        org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    private void renderFailAndDeleteFile(String msg, UploadFile... uploadFiles) {
        renderJson(Ret.fail()
                .set("success", false)
                .setIfNotBlank("message", msg));

        for (UploadFile ufile : uploadFiles) {
            deleteFileQuietly(ufile.getFile());
        }
    }


    public void doDel() {
        doUninstall();
    }

    public void doInstall() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        if (AddonManager.me().install(id)) {
            renderOkJson();
        } else {
            renderJson(Ret.fail().set("message", "插件安装失败，请联系插件开发者。"));
        }
    }

    public void doUninstall() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        if (AddonManager.me().uninstall(id)) {
            renderOkJson();
        } else {
            renderFailJson();
        }
    }

    public void doStart() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }

        if (AddonManager.me().start(id)) {
            renderOkJson();
        } else {
            renderJson(Ret.fail().set("message", "该插件启动时出现异常，启动失败。"));
        }

    }

    public void doStop() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        AddonManager.me().stop(id);
        renderOkJson();
    }


    public void readme(){
        String id = getPara("id");
        AddonInfo addonInfo = AddonManager.me().getAddonInfo(id);
        if (addonInfo.getReadmeText() != null){
            setAttr("content",MarkdownUtils.toHtml(addonInfo.getReadmeText()));
        }
        render("addon/readme.html");
    }

    public void changelog(){
        String id = getPara("id");
        AddonInfo addonInfo = AddonManager.me().getAddonInfo(id);
        if (addonInfo.getChangeLogText() != null){
            setAttr("content",MarkdownUtils.toHtml(addonInfo.getChangeLogText()));
        }
        render("addon/changelog.html");
    }


}
