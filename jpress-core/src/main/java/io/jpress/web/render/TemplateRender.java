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
package io.jpress.web.render;

import com.jfinal.core.JFinal;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.StrUtil;
import io.jboot.web.render.RenderHelpler;
import io.jpress.JPressOptions;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    private static Engine engine;
    private static final String contentType = "text/html; charset=" + getEncoding();
    private static String contextPath = JFinal.me().getContextPath();
    private int errorCode = 0;

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }

    private String cdnDomain = JPressOptions.getCDNDomain();

    public TemplateRender(String view) {
        this.view = view;
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

        String html = getEngine().getTemplate(view).renderToString(data);
        html = replaceSrcPath(html);

        RenderHelpler.renderHtml(response, html, contentType);

    }


    public String toString() {
        return view;
    }


    public String replaceSrcPath(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }


        Document doc = Jsoup.parse(content);
        doc.outputSettings().prettyPrint(false);
        doc.outputSettings().outline(false);

        Elements jsElements = doc.select("script[src]");
        replace(jsElements, "src");

        Elements imgElements = doc.select("img[src]");
        replace(imgElements, "src");

        Elements linkElements = doc.select("link[href]");
        replace(linkElements, "href");

        return doc.toString();

    }

    private void replace(Elements elements, String attrName) {
        Iterator<Element> iterator = elements.iterator();
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return;
        }
        while (iterator.hasNext()) {

            Element element = iterator.next();
            String url = element.attr(attrName);

            if (StrUtil.isBlank(url)
                    || url.startsWith("//")
                    || url.toLowerCase().startsWith("http")
                    || (attrName.equals("src") && url.startsWith("data:image/"))
                    || element.hasAttr("cdn-exclude")
            ) {
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
                url = contextPath + template.getWebAbsolutePath() + url.substring(1);
            }

            // 以 ../ 开头的文件，可能是国际化下面的html资源文件
//            else if (url.startsWith("../")) {
//                url = contextPath + template.getWebAbsolutePath() + url.substring(2);
//            }

            // 直接是文件目录名开头
            else {
                url = contextPath + template.getWebAbsolutePath() + "/" + url;
            }

            if (StrUtil.isNotBlank(cdnDomain)) {
                url = cdnDomain + url;
            }

            element.attr(attrName, url);
        }
    }

}
