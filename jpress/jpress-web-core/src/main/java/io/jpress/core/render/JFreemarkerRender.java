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
package io.jpress.core.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.RenderException;

import freemarker.template.Template;
import io.jpress.core.cache.ActionCacheManager;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public class JFreemarkerRender extends FreeMarkerRender {

	public JFreemarkerRender(String view) {
		super(view);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void render() {

		if (!ActionCacheManager.isEnableCache(request)) {
			super.render();
			return;
		}

		response.setContentType(ActionCacheManager.getCacheContentType(request));

		Map data = new HashMap();
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
			String attrName = attrs.nextElement();
			data.put(attrName, request.getAttribute(attrName));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = null;
		PrintWriter responseWriter = null;
		try {
			osw = new OutputStreamWriter(baos);
			responseWriter = response.getWriter();

			Template template = getConfiguration().getTemplate(view);
			template.process(data, osw);
			osw.flush();

			String renderContent = new String(baos.toByteArray());

			// CDN处理
			renderContent = processCDN(renderContent);

			responseWriter.write(renderContent);
			ActionCacheManager.putCache(request, renderContent);

		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			close(osw, responseWriter);
		}
	}

	private void close(OutputStreamWriter osw, PrintWriter responseWriter) {
		if (osw != null) {
			try {
				osw.close();
			} catch (IOException e) {
			}
		}
		if (responseWriter != null) {
			responseWriter.close();
		}
	}

	private String processCDN(String content) {
		Boolean cdn_enable = OptionQuery.me().findValueAsBool("cdn_enable");
		if (cdn_enable == null || !cdn_enable) {
			return content;
		}

		String cdn_domain = OptionQuery.me().findValue("cdn_domain");
		if (StringUtils.isBlank(cdn_domain)) {
			return content;
		}

		Document doc = Jsoup.parse(content);

		Elements jsElements = doc.select("script[src]");
		srcReplace(jsElements, cdn_domain);

		Elements imgElements = doc.select("img[src]");
		srcReplace(imgElements, cdn_domain);

		Elements linkElements = doc.select("link[href]");
		hrefReplace(linkElements, cdn_domain);

		return doc.toString();

	}

	public static void srcReplace(Elements elements, String cdn_domain) {
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			String src = element.attr("src");
			if (isExcludeFiles(src))
				continue;
			if (src != null && src.startsWith("/")) {
				src = cdn_domain + src;
			}
			element.attr("src", src);
		}
	}

	public static void hrefReplace(Elements elements, String cdn_domain) {
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			String href = element.attr("href");
			if (isExcludeFiles(href))
				continue;
			if (href != null && href.startsWith("/")) {
				href = cdn_domain + href;
			}
			element.attr("href", href);
		}
	}

	private static boolean isExcludeFiles(String link) {
		if (StringUtils.isBlank(link))
			return false;

		String cdn_exclude_files = OptionQuery.me().findValue("cdn_exclude_files");
		if (StringUtils.isNotBlank(cdn_exclude_files) ) {
			if(cdn_exclude_files.contains(link)  
					|| link.contains("/counter")){
				return true;
			}
			
			String[] lines = cdn_exclude_files.split("\\n");
			for(String regex : lines){
				if(StringUtils.match(link, regex)){
					return true;
				}
			}
		}
		
		return false;
	}

}
