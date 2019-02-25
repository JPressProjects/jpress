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

import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
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
    public void index() {

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
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "只支持 .zip 或 .jar 的插件文件"));
            deleteFileQuietly(ufile.getFile());
            return;
        }

        AddonInfo addon = AddonUtil.readSimpleAddonInfo(ufile.getFile());
        if (addon == null || StrUtil.isBlank(addon.getId())) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "无法读取插件配置信息。"));
            deleteFileQuietly(ufile.getFile());
            return;
        }

        File newAddonFile = addon.buildJarFile();

        if (newAddonFile.exists()) {
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "该插件已经存在。"));
            deleteFileQuietly(ufile.getFile());
            return;
        }

        if (!newAddonFile.getParentFile().exists()) {
            newAddonFile.getParentFile().mkdirs();
        }

        try {
            FileUtils.moveFile(ufile.getFile(), newAddonFile);
            AddonManager.me().install(newAddonFile);
            AddonManager.me().start(addon.getId());
        } catch (Exception e) {
            LOG.error("addon install error : ", e);
            renderJson(Ret.fail()
                    .set("success", false)
                    .set("message", "该插件安装失败，请联系管理员。"));
            deleteFileQuietly(ufile.getFile());
            deleteFileQuietly(newAddonFile);
            return;
        }

        renderJson(Ret.ok().set("success", true));
    }

    private void deleteFileQuietly(File file) {
        org.apache.commons.io.FileUtils.deleteQuietly(file);
    }


    public void doDel() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        AddonManager.me().uninstall(id);
        renderJson(Ret.ok());
    }

    public void doInstall() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        if (AddonManager.me().install(id)) {
            renderJson(Ret.ok());
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
        AddonManager.me().uninstall(id);
        renderJson(Ret.ok());
    }

    public void doStart() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        AddonManager.me().start(id);
        renderJson(Ret.ok());
    }

    public void doStop() {
        String id = getPara("id");
        if (StrUtil.isBlank(id)) {
            renderJson(Ret.fail().set("message", "ID数据不能为空"));
            return;
        }
        AddonManager.me().stop(id);
        renderJson(Ret.ok());
    }


}
