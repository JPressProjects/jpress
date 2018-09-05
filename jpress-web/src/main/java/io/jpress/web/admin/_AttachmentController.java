package io.jpress.web.admin;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;

import javax.inject.Inject;
import java.io.File;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/attachment")
public class _AttachmentController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentController.class);

    @Inject
    private AttachmentService as;

    @AdminMenu(text = "所有附件", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void index() {
        Page<Attachment> page = as.paginate(getPagePara(), 15);
        setAttr("page", page);
        render("attachment/list.html");

    }

    @AdminMenu(text = "上传", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void upload() {
        render("attachment/upload.html");
    }


    public void browse() {
        Page<Attachment> page = as.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("attachment/browse.html");
    }

    public void detail() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }

        Attachment attachment = as.findById(id);

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

    public void del() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }
        as.deleteById(id);
        renderJson(Ret.ok());
    }


    public void update() {
        Attachment attachment = getBean(Attachment.class);
        as.saveOrUpdate(attachment);
        renderJson(Ret.ok());
    }


    public void doUpload() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("success", false));
            return;
        }

        String path = AttachmentUtils.moveFile(uploadFile);

        Attachment attachment = new Attachment();
//        attachment.setUserId(getUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtils.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(uploadFile.getContentType());
        attachment.setIp(getIPAddress());
        attachment.setAgent(getUserAgent());

        as.save(attachment);

        renderJson(Ret.ok().set("success", true));
    }


    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 2)
    public void setting() {
        render("attachment/setting.html");
    }

}
