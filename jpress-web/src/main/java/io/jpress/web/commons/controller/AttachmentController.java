package io.jpress.web.commons.controller;

import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.web.base.UserControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/commons/attachment")
public class AttachmentController extends UserControllerBase {


    @Inject
    private AttachmentService as;


    public void upload() {
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
        attachment.setUserId(getLoginedUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtils.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(uploadFile.getContentType());

        as.save(attachment);

        renderJson(Ret.ok().set("success", true).set("src", attachment.getPath()));
    }




}
