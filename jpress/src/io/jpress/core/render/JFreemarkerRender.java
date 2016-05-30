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
import java.util.Map;

import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.RenderException;

import freemarker.template.Template;
import io.jpress.core.cache.ActionCacheManager;

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
			osw = new OutputStreamWriter(baos, "utf-8");
			responseWriter = response.getWriter();

			Template template = getConfiguration().getTemplate(view);
			template.process(data, osw);
			osw.flush();

			String renderContent = new String(baos.toByteArray());
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

}
