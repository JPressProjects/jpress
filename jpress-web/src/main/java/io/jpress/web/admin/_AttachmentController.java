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
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Attachment;
import io.jpress.model.AttachmentCategory;
import io.jpress.service.AttachmentCategoryService;
import io.jpress.service.AttachmentService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @Inject
    private AttachmentCategoryService categoryService;


    @AdminMenu(text = "附件列表", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void list() {
        Columns columns = Columns.create();
        columns.likeAppendPercent("title",getPara("title"));
        columns.eq("category_id",getPara("categoryId"));
        Page<Attachment> page =service._paginateByColumns(getPagePara(),getPageSizePara(), columns);
        setAttr("page", page);

        List<AttachmentCategory> categories = categoryService.findListByColumns(Columns.create(), "order_number asc,id desc");
        setAttr("categories",categories);

        render("attachment/list.html");
    }

    @AdminMenu(text = "上传", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void upload() {
        List<AttachmentCategory> categories = categoryService.findListByColumns(Columns.create(), "order_number asc,id desc");
        setAttr("categories",categories);

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

//    @AdminMenu(text = "分类",groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 9)
    public void category(){
        List<AttachmentCategory> categories = categoryService.findListByColumns(Columns.create(),"order_number asc,id desc");
        setAttr("categories", categories);
        long id = getParaToLong(0, 0L);
        if (id > 0 && categories != null) {
            for (AttachmentCategory category : categories) {
                if (category.getId().equals(id)) {
                    setAttr("category", category);
                }
            }
        }
        render("attachment/category_list.html");
    }


    public void doCategorySave() {
        AttachmentCategory category = getModel(AttachmentCategory.class, "category");

        categoryService.saveOrUpdate(category);
        //更新分类下的内容数量
        categoryService.doUpdateAttachmentCategoryCount(category.getId());
        renderOkJson();
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderOkJson();
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
        String fileNames = getPara("ids");
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
            AttachmentUtils.delete(uploadFile.getFile());
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        File rootFile = new File(PathKit.getWebRootPath(), file.getName());
        if (rootFile.exists()) {
            AttachmentUtils.delete(uploadFile.getFile());
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
//        Page<Attachment> page = service._paginate(getPagePara(), getPageSizePara(), getPara("title"));

        String categoryId = getPara("categoryId");
        setAttr("categoryId",categoryId);

        Page<Attachment> page = service._paginateByColumns(getPagePara(), getPageSizePara(), Columns.create("category_id", categoryId));
        setAttr("page", page);

        List<AttachmentCategory> categories = categoryService.findAll();
        setAttr("categories",categories);

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

        //附件分类
        List<AttachmentCategory> categories = categoryService.findAll();
        setAttr("categories",categories);

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
        Long nowCategoryId = attachment.getCategoryId();

        if (service.delete(attachment)) {
            File attachmentFile = AttachmentUtils.file(attachment.getPath());
            if (attachmentFile.exists() && attachmentFile.isFile()) {
                attachmentFile.delete();
                AliyunOssUtils.delete(new StringBuilder(attachment.getPath()).delete(0, 1).toString());
            }
        }
        //更新分类下的内容数量
        if(nowCategoryId != null){
            categoryService.doUpdateAttachmentCategoryCount(nowCategoryId);
        }

        renderOkJson();
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        Object[] ids = idsSet.toArray();
        if(ids != null && ids.length >0){
            for (Object id : ids) {
                Attachment attachment = service.findById(id);
                if (attachment == null) {
                    renderError(404);
                    return;
                }
                Long nowCategoryId = attachment.getCategoryId();

                if (service.delete(attachment)) {
                    File attachmentFile = AttachmentUtils.file(attachment.getPath());
                    if (attachmentFile.exists() && attachmentFile.isFile()) {
                        attachmentFile.delete();
                        AliyunOssUtils.delete(new StringBuilder(attachment.getPath()).delete(0, 1).toString());
                    }
                }
                //更新分类下的内容数量
                if(nowCategoryId != null){
                    categoryService.doUpdateAttachmentCategoryCount(nowCategoryId);
                }
            }
        }

//        render(service.deleteByIds(idsSet.toArray()) ? OK : FAIL);
        renderOkJson();
    }


    public void doUpdate() {
        Attachment attachment = getBean(Attachment.class);
        service.saveOrUpdate(attachment);

        BigInteger oldCategoryId = getParaToBigInteger("oldCategoryId");

        if(attachment.getCategoryId() != null){
            //更新分类下的内容数量
            categoryService.doUpdateAttachmentCategoryCount(attachment.getCategoryId().longValue());
            if(oldCategoryId != null && !oldCategoryId.equals(attachment.getCategoryId())){
                //更新之前附件分类下的内容数量
                categoryService.doUpdateAttachmentCategoryCount(oldCategoryId.longValue());
            }
        }
        renderOkJson();
    }


}
