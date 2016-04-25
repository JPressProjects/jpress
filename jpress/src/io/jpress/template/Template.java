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

import io.jpress.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.kit.PathKit;

public class Template {

	private String title;
	private String description;
	private String author;
	private String authorWebsite;
	private String version;
	private int versionCode;
	private String updateUrl;
	private String path;
	private String renderType;

	private List<Module> modules;
	private List<Thumbnail> thumbnails;
	private List<String> widgetContainers;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorWebsite() {
		return authorWebsite;
	}

	public void setAuthorWebsite(String authorWebsite) {
		this.authorWebsite = authorWebsite;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public List<Thumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(List<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getRenderType() {
		return renderType;
	}

	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}

	public List<String> getWidgetContainers() {
		if (widgetContainers == null) {
			widgetContainers = new ArrayList<String>();
			List<File> htmlFilelist = new ArrayList<File>();
			scanHtmlFiles(new File(PathKit.getWebRootPath() + path), htmlFilelist);

			for (File htmlFile : htmlFilelist) {
				String htmlText = FileUtils.readString(htmlFile);
				List<String> containers = getWidgetContainer(htmlText);
				for (String c : containers) {
					if (!widgetContainers.contains(c)) {
						widgetContainers.add(c);
					}
				}
			}
		}

		return widgetContainers;
	}

	public void setWidgetContainers(List<String> widgetContainers) {
		this.widgetContainers = widgetContainers;
	}

	public Module getModuleByName(String name) {
		if (modules != null && name != null) {
			for (Module m : modules) {
				if (name.equals(m.getName())) {
					return m;
				}
			}
		}
		return null;
	}

	public Thumbnail getThumbnailByName(String name) {
		if (thumbnails != null && name != null) {
			for (Thumbnail t : thumbnails) {
				if (name.equals(t.getName())) {
					return t;
				}
			}
		}
		return null;
	}

	private void scanHtmlFiles(File file, List<File> fillToList) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (null != files && files.length > 0) {
				for (File f : files) {
					if (f.isDirectory())
						scanHtmlFiles(f, fillToList);
					else if (f.getName().endsWith(".html"))
						fillToList.add(f);
				}
			}
		}
	}

	private static List<String> getWidgetContainer(String text) {
		if (text == null || "".equals(text.trim())) {
			return null;
		}
		Pattern p = Pattern.compile("(?<=<@jp_widgets(\\s)?name=\").*?(?=\"(\\s)?(/)?>)");
		Matcher m = p.matcher(text);
		List<String> list = new ArrayList<String>();
		while (m.find()) {
			list.add(m.group(0));
		}
		return list;
	}

}
