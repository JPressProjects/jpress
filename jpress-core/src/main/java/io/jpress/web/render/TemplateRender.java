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
package io.jpress.web.render;

import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.ext.MixedByteArrayOutputStream;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.JPressOptions;
import io.jpress.SiteContext;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.render
 */
public class TemplateRender extends Render {

    private static String charSet = JFinal.me().getConstants().getEncoding();
    private static Engine engine;
    private static final String contentType = "text/html; charset=" + getEncoding();
    private static String contextPath = JFinal.me().getContextPath();
    private int errorCode = 0;


    private Template currentTemplate = TemplateManager.me().getCurrentTemplate();
    private String cdnDomain = JPressOptions.getCDNDomain();
    private boolean templatePreviewEnable = JPressOptions.isTemplatePreviewEnable();

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }


    public TemplateRender(String view) {
        this.view = view;
    }

    public TemplateRender(String view, boolean templatePreviewEnable) {
        this.view = view;
        this.templatePreviewEnable = templatePreviewEnable;
    }

    public TemplateRender(String view, int code) {
        this.view = view;
        this.errorCode = code;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void render() {
        response.setContentType(getContentType());
        if (errorCode > 0) {
            response.setStatus(errorCode);
        }

        Map<Object, Object> data = new HashMap<>();
        for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements(); ) {
            String attrName = attrs.nextElement();
            data.put(attrName, request.getAttribute(attrName));
        }

        com.jfinal.template.Template template = null;
        try {
            template = getEngine().getTemplate(view);
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("File not found")) {
                renderHtml(buildTemplateNotExistsMessage());
                LogKit.error(ex.toString(), ex);
                return;
            } else {
                throw ex;
            }
        }


        try {
            MixedByteArrayOutputStream baos = new MixedByteArrayOutputStream();
            template.render(data, baos);
            response.getWriter().write(buildNormalHtml(baos.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void renderHtml(String html) {
        try {
            response.getWriter().write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildTemplateNotExistsMessage() {

        String renderView = view.contains("/") ? view.substring(view.lastIndexOf("/") + 1) : view;
        String paraView = JbootControllerContext.get().get("v");
        Template template = TemplateManager.me().getCurrentTemplate();


        StringBuilder msgBuilder = new StringBuilder("<html><head><title>错误：模板文件不存在! </title></head>");
        msgBuilder.append("<body bgcolor='white'>以下模板文件不存在：<br /><br />");

        if (StrUtil.isNotBlank(paraView)) {
            paraView = paraView + ".html";
            msgBuilder.append("----");
            msgBuilder.append(template == null ? paraView : template.buildRelativePath(paraView));
            msgBuilder.append("<br />");
        }

        msgBuilder.append("----");
        msgBuilder.append(template == null ? paraView : template.buildRelativePath(renderView));
        msgBuilder.append("<br />");


        if (!view.equals(template.buildRelativePath(renderView))) {
            msgBuilder.append("----");
            msgBuilder.append(view);
            msgBuilder.append("<br />");
        }

        return msgBuilder.append("</body></html>").toString();
    }


    @Override
    public String toString() {
        return view;
    }


    public String buildNormalHtml(InputStream content) throws IOException {

        Document doc = Jsoup.parse(content, charSet, "");
        if (doc.head().childNodeSize() == 0) {
            return doc.body().html();
        } else {
            String script = "    <script>var jpress = {cpath:'" + JFinal.me().getContextPath() + "',spath:'" + SiteContext.getSitePath() + "'}</script>\n";
            doc.head().append(script);
        }

        doc.outputSettings().prettyPrint(false);
        doc.outputSettings().outline(false);

        Elements jsElements = doc.select("script");
        replace(jsElements, "src");

        Elements imgElements = doc.select("img");
        replace(imgElements, "src");

        Elements sourceElements = doc.select("source");
        replace(sourceElements, "src");

        Elements linkElements = doc.select("link");
        replace(linkElements, "href");

        //开启模板预览功能
        if (templatePreviewEnable && TemplateManager.me().getPreviewTemplate() != null) {
            Elements aElements = doc.select("a");
            replacePreviewHref(aElements);
        }

        return doc.toString();
    }

    private void replace(Elements elements, String attrName) {
        if (currentTemplate == null) {
            return;
        }

        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {

            Element element = iterator.next();
            String url = element.attr(attrName);

            if (StrUtil.isBlank(url)
                    || url.startsWith("//")
                    || url.toLowerCase().startsWith("http")
                    || ("src".equals(attrName) && url.startsWith("data:"))
                    || element.hasAttr("cdn-exclude")) {
                continue;
            }

            // 以 / 开头的，需要添加 contextPath
            if (url.startsWith("/")) {
                if (contextPath.length() > 0 && url.startsWith(contextPath + "/") == false) {
                    url = contextPath + url;
                }
            }

            // 以 ./ 开头的文件，需要添加模板路径
            else if (url.startsWith("./")) {
                url = contextPath + currentTemplate.getRelativePath() + url.substring(1);
            }

            // 直接是文件目录名开头
            else {
                url = contextPath + currentTemplate.getRelativePath() + "/" + url;
            }

            if (StrUtil.isNotBlank(cdnDomain)) {
                url = cdnDomain + url;
            }

            element.attr(attrName, url);
        }
    }

    private void replacePreviewHref(Elements elements) {
        Iterator<Element> iterator = elements.iterator();
        RequestUtil.getCurrentUrl();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String url = element.attr("href");
            element.attr("href", buildUrl(url));
        }
    }


    private String buildUrl(String originalUrl) {
        if (StrUtil.isBlank(originalUrl)
                || originalUrl.toLowerCase().startsWith("http")
                || originalUrl.toLowerCase().startsWith("javascript:")
                || originalUrl.startsWith("//")) {
            return originalUrl;
        }

        String url = originalUrl;
        String anchor = null;

        int anchorIndex = url.indexOf("#");
        if (anchorIndex > 0) {
            url = originalUrl.substring(0, anchorIndex);
            anchor = originalUrl.substring(anchorIndex);
        }

        StringBuilder urlBuilder = new StringBuilder(url);

        if (url.contains("?")) {
            urlBuilder.append("&template=").append(currentTemplate.getId());
        } else {
            urlBuilder.append("?template=").append(currentTemplate.getId());
        }

        if (StrUtil.isNotBlank(anchor)) {
            urlBuilder.append(anchor);
        }

        return urlBuilder.toString();
    }

}
