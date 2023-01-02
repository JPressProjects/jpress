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
package io.jpress.core.attachment;

import com.jfinal.kit.LogKit;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutorService;

/**
 * 负责把远程的附件本地化
 */
public class AttachmentDownloader {

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3, "attachment-download");

    /**
     * 用于下载远程附件，下载成功后 更新 attachment 本身的路径
     *
     * @param attachment
     */
    public static void download(Attachment attachment) {
        fixedThreadPool.execute(() -> {
            doDownload(attachment);
        });
    }

    private static void doDownload(Attachment attachment) {

        if (attachment.isLocal()) {
            return;
        }

        String url = attachment.getPath();
        if (StrUtil.isBlank(url)) {
            return;
        }

        String path = "/attachment" + URI.create(url).getPath();

        File downloadToFile = AttachmentUtils.file(path);

        JbootHttpRequest request = JbootHttpRequest.create(url);
        request.setDownloadFile(downloadToFile);

        JbootHttpResponse response = HttpUtil.handle(request);
        if (response.isError()) {
            LogKit.error("download attachment error by url:" + url);
            return;
        }

        attachment.setMimeType(response.getContentType());
        attachment.setPath(path);
        attachment.update();

    }


}
