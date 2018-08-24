package io.jpress.web.admin;

import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.model.Attachment;
import io.jpress.utils.AttachmentUtils;

import java.io.File;

/**
 * Created by michael on 16/11/30.
 */
@RequestMapping("/ckeditor")
public class _CKEditorController extends AdminControllerBase {

    public void index() {
        renderError(404);
    }


    public void upload() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        Integer CKEditorFuncNum = getParaToInt("CKEditorFuncNum");
        if (CKEditorFuncNum == null) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderText("请提交上传的文件。");
            return;
        }
        File file = uploadFile.getFile();
        if (file != null) {
            int fileSize = Math.round(file.length() / 1024 * 100) / 100;
            if (fileSize > 2048) {
                renderText("图片大小不能超过2MB");
                return;
            }
        }

        String path = AttachmentUtils.moveFile(uploadFile);

        Attachment attachment = new Attachment();
        attachment.setUserId(getUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtils.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(uploadFile.getContentType());
        attachment.setIp(getIPAddress());
        attachment.setAgent(getUserAgent());

        if (attachment.save()) {

            /**
             * <script type="text/javascript">
             window.parent.CKEDITOR.tools.callFunction("0", "", "");
             </script>
             */
            int funcNum = getParaToInt("CKEditorFuncNum");
            StringBuilder textBuilder = new StringBuilder("<script type=\"text/javascript\">");
            textBuilder.append("window.parent.CKEDITOR.tools.callFunction(\"" + funcNum + "\", \"" + attachment.getPath() + "\", \"\");");
            textBuilder.append("</script>");
            renderHtml(textBuilder.toString());
        } else {
            renderText("系统错误");
        }
    }


}
