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
package io.jpress.admin.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.FileUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/tools", viewPath = "/WEB-INF/admin/tools")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ToolsController extends JBaseController {

	public void index() {

	}

	public void druid() {

	}

	public void _import() {

	}

	public void export() {

	}

	public void wechatImport() {

	}

	public void wordpressImport() {
		keepPara();

		if (!isMultipartRequest()) {
			setAttr("modules", TemplateManager.me().currentTemplateModules());
			return;
		}

		UploadFile ufile = getFile();
		if (ufile == null) {
			renderAjaxResultForError("您还未选择WordPress文件");
			return;
		}

		if (!".xml".equals(FileUtils.getSuffix(ufile.getFileName()))) {
			renderAjaxResultForError("请选择从WordPress导出的XML文件");
			return;
		}

		String newPath = AttachmentUtils.moveFile(ufile);
		File xmlFile = new File(PathKit.getWebRootPath(), newPath);

		List<Content> contents = WordPressUtils.parse(xmlFile);
		if (contents == null || contents.size() == 0) {
			renderAjaxResultForError("无法解析WordPress格式，可能是导出有误");
			return;
		}

		String moduelName = getPara("_module");
		if (StringUtils.isBlank(moduelName)) {
			renderAjaxResultForError("请选择导入目标");
			return;
		}

		for (Content c : contents) {
			if (c.getCreated() == null) {
				c.setCreated(new Date());
			}

			String slug = StringUtils.isBlank(c.getSlug()) ? c.getTitle() : c.getSlug();
			c.setSlug(slug);

			if (c.getUserId() == null) {
				User user = getLoginedUser();
				c.setUserId(user.getId());
			}

			if (c.getModule() == null) {
				c.setModule(moduelName);
			}

			c.save();
		}

		renderAjaxResultForSuccess();
	}

	public static class WordPressUtils extends DefaultHandler {
		private static final Log log = Log.getLog(WordPressUtils.class);
		private List<Content> contents;
		private Content content;

		private String value = null;

		public WordPressUtils() {
			contents = new ArrayList<Content>();
		}

		public List<Content> startParse(File wordpressXml) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(wordpressXml, this);
			} catch (Exception e) {
				log.warn("ConfigParser parser exception", e);
			}

			return contents;
		}

		public static List<Content> parse(File wordpressXml) {
			return new WordPressUtils().startParse(wordpressXml);
		}

		@Override
		public void endDocument() throws SAXException {
			// template.setModules(modules);
			// template.setThumbnails(thumbnails);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {

			if ("item".equalsIgnoreCase(qName)) {
				content = new Content();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			if ("item".equalsIgnoreCase(qName)) {
				if (content != null) {
					contents.add(content);
				}
			} else if ("title".equalsIgnoreCase(qName)) {
				if (StringUtils.isNotBlank(value) && content != null)
					content.setTitle(value);
			} else if ("wp:post_type".equalsIgnoreCase(qName)) {
				if (!"post".equals(value)) {
					content = null;
				}
			} else if ("content:encoded".equalsIgnoreCase(qName)) {
				if (content != null) {
					content.setText(value);
				}
			} else if ("wp:status".equalsIgnoreCase(qName)) {
				// 没有发布的文章不导入，比如草稿或者垃圾箱的文章
				if ("publish".equals(value) && content != null) {
					content.setStatus(Content.STATUS_NORMAL);
				} else if ("draft".equals(value) && content != null) {
					content.setStatus(Content.STATUS_DRAFT);
				} else {
					content = null;
				}
			}

		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			value = new String(ch, start, length);
		}
	}

}
