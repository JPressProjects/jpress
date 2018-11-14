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
package io.jpress.commons.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AttachmentUtils {

    /**
     * @param uploadFile
     * @return new file relative path
     */
    public static String moveFile(UploadFile uploadFile) {
        if (uploadFile == null)
            return null;

        File file = uploadFile.getFile();
        if (!file.exists()) {
            return null;
        }

        File newfile = newAttachemnetFile(FileUtils.getSuffix(file.getName()));

        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }

        file.renameTo(newfile);

        String attachmentRoot = StrUtils.isNotBlank(JPressConfig.me.getAttachmentRoot())
                ? JPressConfig.me.getAttachmentRoot()
                : PathKit.getWebRootPath();

        return FileUtils.removePrefix(newfile.getAbsolutePath(), attachmentRoot);
    }

    public static File newAttachemnetFile(String suffix) {

        String attachmentRoot = StrUtils.isNotBlank(JPressConfig.me.getAttachmentRoot())
                ? JPressConfig.me.getAttachmentRoot()
                : PathKit.getWebRootPath();

        String uuid = UUID.randomUUID().toString().replace("-", "");

        StringBuilder newFileName = new StringBuilder(attachmentRoot)
                .append(File.separator).append("attachment")
                .append(File.separator).append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
                .append(File.separator).append(uuid)
                .append(suffix);

        return new File(newFileName.toString());
    }

    static List<String> imageSuffix = new ArrayList<String>();

    static {
        imageSuffix.add(".jpg");
        imageSuffix.add(".jpeg");
        imageSuffix.add(".png");
        imageSuffix.add(".bmp");
        imageSuffix.add(".gif");
        imageSuffix.add(".webp");
    }

    public static boolean isImage(String path) {
        String sufffix = FileUtils.getSuffix(path);
        if (StrUtils.isNotBlank(sufffix))
            return imageSuffix.contains(sufffix.toLowerCase());
        return false;
    }

    public static void main(String[] args) {
        System.out.println(FileUtils.getSuffix("xxx.jpg"));
    }

}
