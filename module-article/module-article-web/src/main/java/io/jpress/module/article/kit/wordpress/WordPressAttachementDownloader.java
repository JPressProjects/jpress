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
package io.jpress.module.article.kit.wordpress;

import io.jboot.Jboot;
import io.jboot.core.http.JbootHttpRequest;
import io.jboot.core.http.JbootHttpResponse;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordPressAttachementDownloader {

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    /**
     * 用于下载 WordPress 的附件，下载成功后 更新 attachment 本身的路径
     *
     * @param attachment
     */
    public static void download(Attachment attachment) {
        fixedThreadPool.execute(() -> {
            doDownload(attachment);
        });
    }

    private static void doDownload(Attachment attachment) {
        if (attachment.isLocal()) return;

        String url = attachment.getPath();

        String path = URI.create(url).getPath();


        JbootHttpRequest request = JbootHttpRequest.create(url, null, JbootHttpRequest.METHOD_GET);
        request.setDownloadFile(AttachmentUtils.file(path));

        JbootHttpResponse response = Jboot.me().getHttp().handle(request);
        if (response.isError() == false) {
            attachment.setMimeType(request.getContentType());
            attachment.setPath(path);
            attachment.update();
        }

    }


}
