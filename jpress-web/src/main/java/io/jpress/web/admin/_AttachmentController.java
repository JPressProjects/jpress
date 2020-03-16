/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/attachment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AttachmentController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentController.class);

    @Inject
    private AttachmentService service;

    @AdminMenu(text = "所有附件", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void index() {
        Page<Attachment> page = service._paginate(getPagePara(), 15, getPara("title"));
        setAttr("page", page);
        render("attachment/list.html");
    }

    @AdminMenu(text = "上传", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void upload() {
        render("attachment/upload.html");
    }


    @AdminMenu(text = "设置", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 2)
    public void setting() {
        render("attachment/setting.html");
    }


    @AdminMenu(text = "根目录", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 99)
    public void root() {
        File rootFile = new File(PathKit.getWebRootPath());
        String name = getPara("name");
        File[] files = StrUtil.isBlank(name)
                ? rootFile.listFiles(pathname -> !pathname.isDirectory())
                : rootFile.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().contains(name));

        setAttr("files", RootFile.toFiles(files));
        render("attachment/root.html");
    }

    public void doDelRootFile() {
        String fileName = getPara("name");
        if (StrUtil.isBlank(fileName) || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            renderFailJson();
            return;
        }

        File file = new File(PathKit.getWebRootPath(), fileName);
        if (file.delete()) {
            renderOkJson();
        } else {
            renderFailJson();
        }
    }

    public void doBatchDelRootFile() {
        String fileNames = getPara("names");
        if (StrUtil.isBlank(fileNames) || fileNames.contains("..") || fileNames.contains("/") || fileNames.contains("\\")) {
            renderFailJson();
            return;
        }

        String[] fileNamaArray = fileNames.split(",");
        for (String fileName : fileNamaArray) {
            if (StrUtil.isNotBlank(fileName)) {
                File file = new File(PathKit.getWebRootPath(), fileName.trim());
                file.delete();
            }
        }
        renderOkJson();
    }

    public void doUplaodRootFile() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("message", "请选择要上传的文件"));
            return;
        }

        File file = uploadFile.getFile();
        if (AttachmentUtils.isUnSafe(file)) {
            file.delete();
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        File rootFile = new File(PathKit.getWebRootPath(), file.getName());
        if (rootFile.exists()) {
            file.delete();
            renderJson(Ret.fail().set("message", "该文件已经存在，请手动删除后再重新上传。"));
            return;
        }

        try {
            FileUtils.moveFile(file, rootFile);
            rootFile.setReadable(true, false);
            renderOkJson();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        renderJson(Ret.fail().set("message", "系统错误。"));
    }

    public static class RootFile {

        private File file;

        public RootFile(File file) {
            this.file = file;
        }


        public String getName() {
            return file.getName();
        }

        public String getSize() {
            long fileLen = file.length();
            String fileLenUnit = " Byte";
            if (fileLen > 1024) {
                fileLen = fileLen / 1024;
                fileLenUnit = " KB";
            }
            if (fileLen > 1024) {
                fileLen = fileLen / 1024;
                fileLenUnit = " MB";
            }
            return fileLen + fileLenUnit;
        }

        public Date getModifiedDate() {
            return new Date(file.lastModified());
        }

        public String getOwner() {
            try {
                FileOwnerAttributeView foav = Files.getFileAttributeView(Paths.get(file.toURI()), FileOwnerAttributeView.class);
                if (foav != null) {
                    UserPrincipal owner = foav.getOwner();
                    if (owner != null) {
                        return owner.getName();
                    }
                }
            } catch (Exception e) {
                LogKit.error(e.toString(), e);
            }
            return "";
        }


        public String getPermission() {
            try {
                PosixFileAttributeView posixView = Files.getFileAttributeView(Paths.get(file.toURI()), PosixFileAttributeView.class);
                if (posixView != null) {
                    PosixFileAttributes attrs = posixView.readAttributes();
                    if (attrs != null) {
                        return PosixFilePermissions.toString(attrs.permissions());
                    }
                }
            } catch (Exception e) {
                LogKit.error(e.toString(), e);
            }
            return "";
        }


        public static List<RootFile> toFiles(File[] files) {
            if (files == null || files.length == 0) {
                return null;
            }
            List<RootFile> rootFiles = new ArrayList<>();
            for (File file : files) {
                rootFiles.add(new RootFile(file));
            }
            return rootFiles;
        }
    }


    public void browse() {
        Page<Attachment> page = service._paginate(getPagePara(), 10, getPara("title"));
        setAttr("page", page);
        render("attachment/browse.html");
    }


    public void detail() {
        Long id = getIdPara();

        Attachment attachment = service.findById(id);

        setAttr("attachment", attachment);

        File attachmentFile = new File(PathKit.getWebRootPath(), attachment.getPath());
        setAttr("attachmentName", attachmentFile.getName());

        long fileLen = attachmentFile.length();
        String fileLenUnit = "Byte";
        if (fileLen > 1024) {
            fileLen = fileLen / 1024;
            fileLenUnit = "KB";
        }
        if (fileLen > 1024) {
            fileLen = fileLen / 1024;
            fileLenUnit = "MB";
        }
        setAttr("attachmentSize", fileLen + fileLenUnit);
        try {
            if (AttachmentUtils.isImage(attachment.getPath())) {
                String ratio = ImageUtils.ratioAsString(attachmentFile.getAbsolutePath());
                setAttr("attachmentRatio", ratio == null ? "unknow" : ratio);
            }
        } catch (Throwable e) {
            LOG.error("detail() ratioAsString error", e);
        }

        render("attachment/detail.html");
    }

    public void doDel() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }

        Attachment attachment = service.findById(id);
        if (attachment == null) {
            renderError(404);
            return;
        }

        if (service.delete(attachment)) {
            File attachmentFile = AttachmentUtils.file(attachment.getPath());
            if (attachmentFile.exists() && attachmentFile.isFile()) {
                attachmentFile.delete();
                AliyunOssUtils.delete(new StringBuilder(attachment.getPath()).delete(0, 1).toString());
            }
        }

        renderOkJson();
    }


    public void doUpdate() {
        Attachment attachment = getBean(Attachment.class);
        service.saveOrUpdate(attachment);
        renderOkJson();
    }


}
