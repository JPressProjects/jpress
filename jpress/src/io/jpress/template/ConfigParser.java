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
package io.jpress.template;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import io.jpress.template.Module.TaxonomyType;
import io.jpress.utils.FileUtils;

public class ConfigParser extends DefaultHandler {
	private static final Log log = Log.getLog(ConfigParser.class);
	final Template template;

	private Module cModule;
	private List<Module> modules;
	private List<TaxonomyType> cTaxonomys;
	private List<Thumbnail> thumbnails;

	private String value = null;
	public ConfigParser() {
		template = new Template();
		modules = new ArrayList<Module>();
		thumbnails = new ArrayList<Thumbnail>();
	}

	public Template parser(String tName) {
		String path = PathKit.getWebRootPath() + "/templates/" + tName;
		File configFile = getConfigFile(new File(path));
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(configFile, this);
		} catch (Exception e) {
			log.warn("ConfigParser parser exception", e);
		}
		
		String screenshot = path +"/screenshot.png";
		if(!new File(screenshot).exists()){
			screenshot = null;
		}
		
		path = FileUtils.removeRootPath(configFile.getParent());
		template.setPath(path.replace("\\", "/"));
		
		if(screenshot!=null){
			template.setScreenshot(template.getPath()+"/screenshot.png");
		}
		
		return template;
	}

	private File getConfigFile(File file) {
		if (file.isDirectory()) {
			File configFile = new File(file, "config.xml");

			if (configFile.exists() && configFile.isFile()) {
				return configFile;
			} else {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File f : files) {
						if (f.isDirectory()) {
							File cf = getConfigFile(f);
							if (cf != null) {
								return cf;
							}
						}

					}
				}
			}
		}
		return null;
	}

	@Override
	public void endDocument() throws SAXException {
		template.setModules(modules);
		template.setThumbnails(thumbnails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {

		if ("module".equalsIgnoreCase(qName)) {
			cModule = new Module();
			cModule.setAddTitle(attrs.getValue("add"));
			cModule.setName(attrs.getValue("name"));
			cModule.setListTitle(attrs.getValue("list"));
			cModule.setTitle(attrs.getValue("title"));
			cModule.setCommentTitle(attrs.getValue("comment"));

			cTaxonomys = new ArrayList<Module.TaxonomyType>();
		}

		if ("taxonomy".equalsIgnoreCase(qName)) {
			TaxonomyType tt = new TaxonomyType(cModule);

			tt.setName(attrs.getValue("name"));
			tt.setTitle(attrs.getValue("title"));
			tt.setFormType(attrs.getValue("formType"));

			cTaxonomys.add(tt);
		}

		if ("thumbnail".equalsIgnoreCase(qName)) {
			Thumbnail tb = new Thumbnail();
			tb.setName(attrs.getValue("name"));
			tb.setSize(attrs.getValue("size"));
			thumbnails.add(tb);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if ("module".equalsIgnoreCase(qName)) {
			cModule.setTaxonomyTypes(cTaxonomys);
			modules.add(cModule);
		}
		
		else if ("title".equalsIgnoreCase(qName)) {
			template.setTitle(value);
		} else if ("description".equalsIgnoreCase(qName)) {
			template.setDescription(value);
		} else if ("author".equalsIgnoreCase(qName)) {
			template.setAuthor(value);
		} else if ("authorWebsite".equalsIgnoreCase(qName)) {
			template.setAuthorWebsite(value);
		} else if ("version".equalsIgnoreCase(qName)) {
			template.setVersion(value);
		} else if ("renderType".equalsIgnoreCase(qName)) {
			template.setRenderType(value);
		}else if ("versionCode".equalsIgnoreCase(qName)) {
			int versionCode = 0;
			try {
				versionCode = Integer.parseInt(value.trim());
			} catch (Exception e) {}
			template.setVersionCode(versionCode);
		} else if ("updateUrl".equalsIgnoreCase(qName)) {
			template.setUpdateUrl(value);
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		value = new String(ch, start, length);

	}

}
