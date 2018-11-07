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

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.web.base.UserControllerBase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 16/11/30.
 */
@RequestMapping("/commons/ckeditor")
public class CKEditorController extends UserControllerBase {

    public void index() {
        renderError(404);
    }


    public void upload() {

        if (!isMultipartRequest()) {
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
        attachment.setUserId(getLoginedUser().getId());
        attachment.setTitle(uploadFile.getOriginalFileName());
        attachment.setPath(path.replace("\\", "/"));
        attachment.setSuffix(FileUtils.getSuffix(uploadFile.getFileName()));
        attachment.setMimeType(uploadFile.getContentType());

        if (attachment.save()) {

            /**
             * {"fileName":"1.jpg","uploaded":1,"url":"\/userfiles\/images\/1.jpg"}
             */
            Map map = new HashMap();
            map.put("fileName", attachment.getTitle());
            map.put("uploaded", 1);
            map.put("url", JFinal.me().getContextPath() + attachment.getPath());
            renderJson(map);
        } else {
            renderText("系统错误");
        }
    }


}
