package io.jpress.web.admin;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.utils.AttachmentUtils;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/attachment")
public class _AttachmentController extends AdminControllerBase {

    @Inject
    private AttachmentService as;

    @AdminMenu(text = "所有附件", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void index() {
        Page<Attachment> page = as.paginate(getParaToInt("page", 1), 10);
        setAttr("page", page);
        render("attachment/list.html");

    }

    @AdminMenu(text = "上传", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void upload() {
        render("attachment/upload.html");
    }


    public void browse(){
        Page<Attachment> page = as.paginate(getParaToInt("page", 1), 10);
        setAttr("page", page);
        render("attachment/browse.html");
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

        attachment.save();

        renderJson(Ret.ok().set("success", true));
    }


    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 2)
    public void setting() {
        render("attachment/setting.html");
    }

}
