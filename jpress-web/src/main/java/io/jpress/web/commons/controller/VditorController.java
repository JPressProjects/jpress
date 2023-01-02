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
package io.jpress.web.commons.controller;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.service.OptionService;
import io.jpress.web.base.UserControllerBase;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 16/11/30.
 */
@RequestMapping("/commons/vditor")
public class VditorController extends UserControllerBase {

    @Inject
    private OptionService optionService;

    @Inject
    private AttachmentService attachmentService;

    public void index() {
        renderError(404);
    }


    public void upload() {

        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        List<UploadFile> uploadFiles = getFiles();
        Map<String, String> fileNameAndPaths = new HashMap<>();

        uploadFiles.forEach(uploadFile -> processUploadFile(uploadFile, fileNameAndPaths));

        Map data = new HashMap();
        data.put("succMap", fileNameAndPaths);

        Map ret = new HashMap();
        ret.put("code", 0);
        ret.put("data", data);

        renderJson(ret);
    }


    private void processUploadFile(UploadFile uploadFile, Map<String, String> fileNameAndPaths) {

        if (uploadFile == null) {
            renderJson(Ret.of("error", Ret.of("message", "请选择要上传的文件")));
            return;
        }


        File file = uploadFile.getFile();
        if (!getLoginedUser().isStatusOk()) {
            AttachmentUtils.delete(file);
            renderJson(Ret.of("error", Ret.of("message", "当前用户未激活，不允许上传任何文件。")));
            return;
        }


        if (AttachmentUtils.isUnSafe(file)) {
            AttachmentUtils.delete(file);
            renderJson(Ret.of("error", Ret.of("message", "不支持此类文件上传")));
            return;
        }


        String mineType = uploadFile.getContentType();
        String fileType = mineType.split("/")[0];

        int maxImgSize = JPressOptions.getAsInt("attachment_img_maxsize", 10);
        int maxOtherSize = JPressOptions.getAsInt("attachment_other_maxsize", 100);
        Integer maxSize = "image".equals(fileType) ? maxImgSize : maxOtherSize;
        int fileSize = Math.round(file.length() / 1024 * 100) / 100;

        if (maxSize > 0 && fileSize > maxSize * 1024) {
            AttachmentUtils.delete(file);
            renderJson(Ret.of("error", Ret.of("message", "上传文件大小不能超过 " + maxSize + " MB")));
            return;
        }


        String path = AttachmentUtils.moveFile(uploadFile);
        AliyunOssUtils.upload(path, AttachmentUtils.file(path));

        Attachment attachment = new Attachment();
        attachment.setUserId(getLoginedUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtil.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(mineType);

        if (attachmentService.save(attachment) != null) {
            String url = JFinal.me().getContextPath() + attachment.getPath();
            String alt = attachment.getTitle();
            fileNameAndPaths.put(alt, url);
        }
    }


}
