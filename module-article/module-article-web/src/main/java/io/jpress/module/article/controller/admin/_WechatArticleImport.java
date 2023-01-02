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
package io.jpress.module.article.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.utils.FileUtil;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.model.Attachment;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.AdminControllerBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.controller
 */
@RequestMapping(value = "/admin/setting/tools/wechat", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _WechatArticleImport extends AdminControllerBase {

    public void index() {
        render("article/wechat.html");
    }


    public void doImport() {

        ApiResult apiResult = MediaApi.getMaterialCount();
        if (!apiResult.isSucceed()) {
            renderJson(Ret.fail().set("message", "无法获取公众号文章信息，请查看公众号配置是否正确。"));
            return;
        }

        int articleCount = apiResult.getInt("news_count");
        doSyncArticles(articleCount);

        renderJson(Ret.ok().set("message", "后台正在为您同步 " + articleCount + " 篇文章及其附件，请稍后查看。"));
    }


    private void doSyncArticles(final int articleCount) {
        new Thread(() -> {
            int times = articleCount <= 20 ? 1 : articleCount / 20;

            List<Article> articles = new ArrayList<>();
            List<String> images = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                ApiResult newsResult = MediaApi.batchGetMaterialNews(i * 20, 20);
                if (newsResult.isSucceed()) {
                    JSONArray jsonArray = newsResult.get("item");
                    for (int j = 0; j < jsonArray.size(); j++) {
                        JSONArray array = jsonArray.getJSONObject(j).getJSONObject("content").getJSONArray("news_item");
                        for (int a = 0; a < array.size(); a++) {
                            JSONObject articleObject = array.getJSONObject(a);
                            String title = articleObject.getString("title");
                            String content = articleObject.getString("content");
                            String summary = articleObject.getString("digest");

                            if (StrUtil.isNotBlank(content)) {
                                content = processContentImages(content, images);
                            }

                            Article article = new Article();
                            article.setTitle(title);
                            article.setContent(content);
                            article.setSummary(summary);
                            article.setCreated(new Date());
                            article.setModified(new Date());
                            article.setStatus(Article.STATUS_NORMAL);

                            articles.add(article);
                        }
                    }
                }
            }

            doSaveArticles(articles);
            doDownloadImages(images);

        }).start();
    }

    private String processContentImages(String content, List<String> imageUrls) {

        Document doc = Jsoup.parse(content);
        Elements imgElements = doc.select("img");
        if (imgElements != null) {
            Iterator<Element> iterator = imgElements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();

                String imageUrl = element.hasAttr("src")
                        ? element.attr("src")
                        : element.attr("data-src");

//http://mmbiz.qpic.cn/mmbiz/4gZTdZfnQeDvQqCZFuVvYv8scGS7sEQTRETgISib1blz5iclAtnsccaJhaugmKc
// hhm8mFOtjnicibibumazy8wPS6Xg/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1

                imageUrl = replaceLast(imageUrl, "/", "__");
                imageUrl = imageUrl.startsWith("http://")
                        ? imageUrl.replace("http://", "/attachment/")
                        : imageUrl.replace("https://", "/attachment/s");

                imageUrl = imageUrl.replace("?",".png?");

                element.removeAttr("data-src");
                element.attr("src",imageUrl);

                imageUrls.add(imageUrl);
            }
        }

        return doc.toString();
    }


    private void doSaveArticles(List<Article> articles) {

        ArticleService service = Aop.get(ArticleService.class);

        for (Article article : articles) {

            if (article.getCreated() == null) {
                article.setCreated(new Date());
                article.setModified(new Date());
            }

            if (service.findByTitle(article.getTitle()) == null) {
                service.save(article);
            }
        }
    }

    private void doDownloadImages(List<String> urls) {
        for (String url : urls) {
            WechatArticleImageDownloader.download(url);
        }
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }


    public static class WechatArticleImageDownloader {

        private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3,"wechat-image-downloader");

        public static void download(String url) {
            fixedThreadPool.execute(() -> doDownload(url));
        }

        private static void doDownload(String url) {

            String remoteUrl = url.startsWith("/attachment/s")
                    ?url.replace("/attachment/s","https://")
                    :url.replace("/attachment/", "http://");

            remoteUrl = replaceLast(remoteUrl, "__", "/")
                    .replace(".png?","?");

            String path = URI.create(url).getPath();
            File downloadToFile = AttachmentUtils.file(path);
            if (downloadToFile.exists() && downloadToFile.length() > 0) {
                return;
            }

            JbootHttpRequest request = JbootHttpRequest.create(remoteUrl);
            request.setDownloadFile(downloadToFile);

            JbootHttpResponse response = HttpUtil.handle(request);
            if (response.isError()) {
                LogKit.error("download attachment error by url:" + remoteUrl);
                return;
            }

            Attachment attachment = new Attachment();
            attachment.setMimeType(response.getContentType());
            attachment.setPath(path);
            attachment.setSuffix(FileUtil.getSuffix(path));
            attachment.setTitle(downloadToFile.getName());
            attachment.save();
        }

    }


}
