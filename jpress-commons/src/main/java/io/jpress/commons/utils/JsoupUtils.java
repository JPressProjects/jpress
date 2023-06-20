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
package io.jpress.commons.utils;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JsoupUtils {


    public static String getFirstImageSrc(String html) {
        return getFirstQuerySrc(html, "img");
    }

    public static String getFirstVideoSrc(String html) {
        return getFirstQuerySrc(html, "video");
    }

    public static String getFirstAudioSrc(String html) {
        return getFirstQuerySrc(html, "audio");
    }

    public static String getFirstQuerySrc(String html, String query) {
        if (StrUtil.isBlank(html)) {
            return null;
        }

        Elements es = Jsoup.parseBodyFragment(html).select(query);
        if (es != null && es.size() > 0) {
            String src = es.first().attr("src");
            return StrUtil.isBlank(src) ? null : src;
        }

        return null;
    }


    public static List<String> getImageSrcs(String html) {
        if (StrUtil.isBlank(html)) {
            return null;
        }

        List<String> list = new ArrayList<String>();

        Document doc = Jsoup.parseBodyFragment(html);
        Elements es = doc.select("img");
        if (es != null && es.size() > 0) {
            for (Element e : es) {
                String src = e.attr("src");
                if (StrUtil.isNotBlank(src)) {
                    list.add(src);
                }
            }
        }
        return list.isEmpty() ? null : list;
    }


    public static String getText(String html) {
        if (StrUtil.isBlank(html)) {
            return html;
        }
        return StrUtil.escapeHtml(Jsoup.parse(html).text());
    }

    public static void clean(Model model, String... attrs) {
        if (attrs != null && attrs.length == 0) {
            return;
        }
        Set<String> attrNames = CPI.getModifyFlag(model);
        for (String attrName : attrNames) {
            if (CommonsUtils.containsAttr(attrs, attrName)) {
                Object data = model.get(attrName);
                if (!(data instanceof String)) {
                    continue;
                }
                model.set(attrName, clean((String) data));
            }
        }
    }

    private static MyWhitelist whitelist = new MyWhitelist();

    public static String clean(String html) {
        if (StrUtil.isNotBlank(html)) {
            return Jsoup.clean(html, whitelist);
        }

        return html;
    }

    /**
     * 做自己的白名单，允许base64的图片通过等
     *
     * @author michael
     */
    public static class MyWhitelist extends org.jsoup.safety.Safelist {

        public MyWhitelist() {

            addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "span", "embed", "object", "dl", "dt",
                    "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "figure", "style", "section",
                    "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul");

            addAttributes("div", "data-form-id");
            addAttributes("a", "href", "title", "target");
            addAttributes("blockquote", "cite");
            addAttributes("col", "span");
            addAttributes("colgroup", "span");
            addAttributes("img", "align", "alt", "src", "title");
            addAttributes("ol", "start");
            addAttributes("q", "cite");
            addAttributes("font", "face");
            addAttributes("table", "summary");
            addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
            addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width");
            addAttributes("video", "src", "autoplay", "controls", "loop", "muted", "poster", "preload", "data-vid", "data-cloud", "data-play-auth", "data-app-id");
            addAttributes("object", "width", "height", "classid", "codebase");
            addAttributes("param", "name", "value");
            addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage");

            addAttributes(":all", "class", "style", "height", "width", "type", "id", "name", "data-id", "data-role", "data-width", "data-height");

//
            addProtocols("blockquote", "cite", "http", "https");
            addProtocols("cite", "cite", "http", "https");
            addProtocols("q", "cite", "http", "https");

            //如果添加以下的协议，那么href 必须是http、 https 等开头，相对路径则被过滤掉了
            //addProtocols("a", "href", "ftp", "http", "https", "mailto", "tel");

            //如果添加以下的协议，那么src必须是http 或者 https 开头，相对路径则被过滤掉了，
            //所以必须注释掉，允许相对路径的图片资源
            //addProtocols("img", "src", "http", "https");
        }

        @Override
        protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {

            //不允许 javascript 开头的 src 和 href
            if ("src".equalsIgnoreCase(attr.getKey()) || "href".equalsIgnoreCase(attr.getKey())) {
                String value = attr.getValue();
                if (StrUtil.isNotBlank(value) && value.toLowerCase().startsWith("javascript")) {
                    return false;
                }
            }


            //允许 base64 的图片内容
            if ("img".equals(tagName) && "src".equals(attr.getKey()) && attr.getValue().startsWith("data:") && attr.getValue().contains("base64")) {
                return true;
            }

            return super.isSafeAttribute(tagName, el, attr);
        }
    }


}