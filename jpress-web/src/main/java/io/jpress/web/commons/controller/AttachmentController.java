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
package io.jpress.web.commons.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.web.base.UserControllerBase;

import java.io.File;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/commons/attachment")
public class AttachmentController extends UserControllerBase {


    @Inject
    private AttachmentService service;

    public void upload() {
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
        if (!getLoginedUser().isStatusOk()) {
            file.delete();
            renderJson(Ret.create("error", Ret.create("message", "当前用户未激活，不允许上传任何文件。")));
            return;
        }

        if (AttachmentUtils.isUnSafe(file)) {
            file.delete();
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        String mineType = uploadFile.getContentType();
        String fileType = mineType.split("/")[0];

        Integer maxImgSize = JPressOptions.getAsInt("attachment_img_maxsize", 2);
        Integer maxOtherSize = JPressOptions.getAsInt("attachment_other_maxsize", 10);

        Integer maxSize = "image".equals(fileType) ? maxImgSize : maxOtherSize;

        int fileSize = Math.round(file.length() / 1024 * 100) / 100;
        if (maxSize != null && maxSize > 0 && fileSize > maxSize * 1024) {
            file.delete();
            renderJson(Ret.fail().set("message", "上传文件大小不能超过 " + maxSize + " MB"));
            return;
        }

        String path = AttachmentUtils.moveFile(uploadFile);
        AliyunOssUtils.upload(path, AttachmentUtils.file(path));

        Attachment attachment = new Attachment();
        attachment.setUserId(getLoginedUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtil.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(uploadFile.getContentType());

        service.save(attachment);

        renderJson(Ret.ok().set("success", true).set("src", attachment.getPath()));
    }


}
