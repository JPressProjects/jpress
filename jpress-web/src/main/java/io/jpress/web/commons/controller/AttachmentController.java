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
package io.jpress.web.commons.controller;

import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.service.OptionService;
import io.jpress.web.base.UserControllerBase;

import javax.inject.Inject;
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
    private AttachmentService as;
    @Inject
    OptionService optionService;

    public void upload() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("message", "请提交上传的文件"));
            return;
        }

        String mineType = uploadFile.getContentType();
        String fileType = mineType.split("/")[0];
        Integer maxImgSize = optionService.findAsIntegerByKey("attachment_img_maxsize");
        Integer maxOtherSize = optionService.findAsIntegerByKey("attachment_other_maxsize");
        maxImgSize = maxImgSize == null ? 2 : maxImgSize; //没设置 默认2M
        maxOtherSize = maxOtherSize == null ? 20 : maxOtherSize; //没设置 默认20M
        Integer maxSize = fileType.equals("image") ? maxImgSize : maxOtherSize;
        File file = uploadFile.getFile();
        if (file != null) {
            int fileSize = Math.round(file.length() / 1024 * 100) / 100;
            if (fileSize > maxSize*1024) {
                renderJson(Ret.fail().set("message", "文件大小不能超过" + maxSize + "MB"));
                return;
            }
        }
        String path = AttachmentUtils.moveFile(uploadFile);
        AliyunOssUtils.upload(path, AttachmentUtils.file(path));


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
