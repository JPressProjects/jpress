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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
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
import io.jpress.Consts;
import io.jpress.core.Jpress;
import io.jpress.core.cache.ActionCacheManager;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public class JFreemarkerRender extends FreeMarkerRender {

	public JFreemarkerRender(String view) {
		super(view);
	}

	@SuppressWarnings("rawtypes")
	public void renderWithoutCache(Map data) {
		PrintWriter writer = null;
		try {
			Template template = getConfiguration().getTemplate(view);
			writer = response.getWriter();
			template.process(data, writer); // Merge the data-model and the
											// template
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void render() {

		Map<String, Object> jpressTags = new HashMap<String, Object>();
		jpressTags.putAll(Jpress.jpressTags);

		Map data = new HashMap();
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
			String attrName = attrs.nextElement();
			if (attrName.startsWith("jp.")) {
				jpressTags.put(attrName.substring(3), request.getAttribute(attrName));
			} else {
				data.put(attrName, request.getAttribute(attrName));
			}
		}

		data.put("jp", jpressTags);

		if (!ActionCacheManager.isEnableCache(request)) {
			response.setContentType(getContentType());
			renderWithoutCache(data);
			return;
		}

		response.setContentType(ActionCacheManager.getCacheContentType(request));
		PrintWriter responseWriter = null;
		try {
			String htmlContent = getHtmlContent(data);
			htmlContent = processCDN(htmlContent); // CDN处理

			responseWriter = response.getWriter();
			responseWriter.write(htmlContent);
			ActionCacheManager.putCache(request, htmlContent);
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			close(responseWriter);
		}
	}

	@SuppressWarnings("rawtypes")
	private String getHtmlContent(Map data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osWriter = null;
		try {
			osWriter = new OutputStreamWriter(baos, Consts.CHARTSET_UTF8);
			Template template = getConfiguration().getTemplate(view);
			template.process(data, osWriter);
			osWriter.flush();
			return baos.toString(Consts.CHARTSET_UTF8);
		} catch (Exception e) {
			if (Jpress.isDevMode()) {
				e.printStackTrace();
			}
			throw new RenderException(e);
		} finally {
			close(baos);
			close(osWriter);
		}
	}

	private void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	private void close(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	private String processCDN(String content) {
		Boolean cdnEnable = OptionQuery.me().findValueAsBool("cdn_enable");
		if (cdnEnable == null || !cdnEnable) {
			return content;
		}

		String cdnDomain = OptionQuery.me().findValue("cdn_domain");
		if (StringUtils.isBlank(cdnDomain)) {
			return content;
		}

		Document doc = Jsoup.parse(content);

		Elements jsElements = doc.select("script[src]");
		replace(jsElements, "src", cdnDomain);

		Elements imgElements = doc.select("img[src]");
		replace(imgElements, "src", cdnDomain);

		Elements linkElements = doc.select("link[href]");
		replace(linkElements, "href", cdnDomain);

		return doc.toString();

	}

	public static void replace(Elements elements, String attrName, String cdnDomain) {
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			String url = element.attr(attrName);
			if (isExcludeUrl(url))
				continue;
			if (url != null && url.startsWith("/")) {
				url = cdnDomain + url;
			}
			element.attr(attrName, url);
		}
	}

	private static boolean isExcludeUrl(String url) {
		if (StringUtils.isBlank(url))
			return false;

		String cdn_exclude_files = OptionQuery.me().findValue("cdn_exclude_files");
		if (StringUtils.isNotBlank(cdn_exclude_files)) {
			if (cdn_exclude_files.contains(url) || url.contains("/counter")) {
				return true;
			}

			String[] lines = cdn_exclude_files.split("\\n");
			for (String regex : lines) {
				if (StringUtils.match(url, regex)) {
					return true;
				}
			}
		}

		return false;
	}

}
