/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtils {

	public static String getFirstImageSrc(String html) {
		if (html == null)
			return null;

		Elements es = Jsoup.parseBodyFragment(html).select("img");
		if (es != null && es.size() > 0)
			return es.first().attr("src");

		return null;
	}

	public static List<String> getImageSrcs(String html) {
		if (StringUtils.isBlank(html)) {
			return null;
		}

		List<String> list = new ArrayList<String>();

		Document doc = Jsoup.parseBodyFragment(html);
		Elements es = doc.select("img");
		if (es != null && es.size() > 0) {
			for (Element e : es) {
				list.add(e.attr("src"));
			}
		}
		return list;
	}

	public static String getText(String html) {
		return Jsoup.parse(html).text();
	}

	public static String getBodyHtml(String html) {
		if (StringUtils.isNotBlank(html)) {
			Document document = Jsoup.parse(html);
			if (null != document && document.body() != null) {
				return document.body().html().toString();
			}
		}
		return html;
	}

	static MyWhitelist whitelist = new MyWhitelist();

	public static String clear(String html) {
		if (StringUtils.isNotBlank(html))
			return Jsoup.clean(html, whitelist);

		return html;
	}

	/**
	 * 做自己的白名单，允许base64的图片通过等
	 * 
	 * @author michael
	 */
	public static class MyWhitelist extends org.jsoup.safety.Whitelist {
		public MyWhitelist() {

			addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt",
					"em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small",
					"strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul");

			addAttributes("a", "href", "title", "target");
			addAttributes("blockquote", "cite");
			addAttributes("col", "span");
			addAttributes("colgroup", "span");
			addAttributes("img", "align", "alt", "src", "title");
			addAttributes("ol", "start");
			addAttributes("q", "cite");
			addAttributes("table", "summary");
			addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
			addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width");
			addAttributes("video", "src", "autoplay", "controls", "loop", "muted", "poster", "preload");

			addAttributes(":all", "class");
			addAttributes(":all", "style");
			addAttributes(":all", "height");
			addAttributes(":all", "width");
			addAttributes(":all", "type");
			addAttributes(":all", "id");
			addAttributes(":all", "name");

			addProtocols("a", "href", "ftp", "http", "https", "mailto", "tel");
			addProtocols("blockquote", "cite", "http", "https");
			addProtocols("cite", "cite", "http", "https");
			addProtocols("img", "src", "http", "https");
			addProtocols("q", "cite", "http", "https");
		}

		@Override
		protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
			return ("img".equals(tagName) && "src".equals(attr.getKey()) && attr.getValue().startsWith("data:;base64"))
					|| super.isSafeAttribute(tagName, el, attr);
		}
	}

}
