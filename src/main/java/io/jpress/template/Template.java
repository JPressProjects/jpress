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

import java.util.List;

public class Template {

	private String id;
	private String title;
	private String description;
	private String author;
	private String authorWebsite;
	private String version;
	private int versionCode;
	private String updateUrl;
	private String path;
	private String renderType;
	private String screenshot;
	

	private List<TplModule> modules;
	private List<Thumbnail> thumbnails;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public List<TplModule> getModules() {
		return modules;
	}

	public void setModules(List<TplModule> modules) {
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
	

	public String getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}


	public TplModule getModuleByName(String name) {
		if (modules != null && name != null) {
			for (TplModule m : modules) {
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


}
