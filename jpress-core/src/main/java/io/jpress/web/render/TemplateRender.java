package io.jpress.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.StringUtils;
import io.jboot.web.render.RenderHelpler;
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

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }

    private static String cdnDomain;

    public static void initCdnDomain(String cdnDomain) {
        TemplateRender.cdnDomain = cdnDomain;
    }


    public TemplateRender(String view) {
        this.view = view;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void render() {
        response.setContentType(getContentType());

        Map<Object, Object> data = new HashMap<Object, Object>();
        for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements(); ) {
            String attrName = attrs.nextElement();
            data.put(attrName, request.getAttribute(attrName));
        }

        String html = getEngine().getTemplate(view).renderToString(data);
        html = replaceSrcTemplateSrcPath(html);

        RenderHelpler.actionCacheExec(html, contentType);
        RenderHelpler.renderHtml(response, html, contentType);

    }


    public String toString() {
        return view;
    }


    public static String replaceSrcTemplateSrcPath(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }


        Document doc = Jsoup.parse(content);

        Elements jsElements = doc.select("script[src]");
        replace(jsElements, "src");

        Elements imgElements = doc.select("img[src]");
        replace(imgElements, "src");

        Elements lazyElements = doc.select("img[data-original]");
        replace(lazyElements, "data-original");

        Elements linkElements = doc.select("link[href]");
        replace(linkElements, "href");

        return doc.toString();

    }

    private static void replace(Elements elements, String attrName) {
        Iterator<Element> iterator = elements.iterator();
        Template template = TemplateManager.me().getCurrentTemplate();
        while (iterator.hasNext()) {

            Element element = iterator.next();
            String url = element.attr(attrName);

            if (StringUtils.isBlank(url)
                    || url.startsWith("//")
                    || url.toLowerCase().startsWith("http")) {
                continue;
            }

            if (url.startsWith("/")) {
                if (cdnDomain != null) {
                    element.attr(attrName, cdnDomain + url);
                }
                continue;
            }


            if (StringUtils.isBlank(url)
                    || url.startsWith("/")
                    || url.startsWith("//")
                    || url.toLowerCase().startsWith("http")) {
                continue;
            }

            if (url.startsWith("./")) {
                url = template.getWebAbsolutePath() + url.substring(1);
            } else {
                url = template.getWebAbsolutePath() + "/" + url;
            }

            if (cdnDomain == null) {
                element.attr(attrName, url);
            } else {
                element.attr(attrName, cdnDomain + url);
            }


        }
    }

}
