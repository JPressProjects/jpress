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
package io.jpress.core.addon;

public class AddonInfo {

	private String id;
	private String jarPath;
	private String addonClass;
	private String title;
	private String description;
	private String author;
	private String authorWebsite;
	private String version;
	private int versionCode;
	private String updateUrl;
	private boolean hasError = false;
	private boolean start = false;

	private IAddon addon;
	private final Hooks hooks = new Hooks();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	public String getAddonClass() {
		return addonClass;
	}

	public void setAddonClass(String addonClass) {
		this.addonClass = addonClass;
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

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public IAddon getAddon() {
		return addon;
	}

	public void setAddon(IAddon addon) {
		this.addon = addon;
	}

	public Hooks getHooks() {
		return hooks;
	}

	public boolean getHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public boolean isStart() {
		return start;
	}

	public boolean start() {
		if (addon != null) {
			return false;
		}

		try {
			start = addon.onStart(hooks);
		} catch (Throwable e) {
			// has exception
			start = false;
		}

		return start;
	}

	public boolean stop() {
		if (addon != null) {
			return false;
		}

		try {
			boolean isStoped = addon.onStop();
			if (isStoped == true) {
				start = false;
			}
			return isStoped;
		} catch (Throwable e) {
		}

		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof AddonInfo)) {
			return false;
		}

		AddonInfo addon = (AddonInfo) obj;
		if (addon.getId() == null) {
			return false;
		}

		return addon.getId().equals(getId());
	}

	@Override
	public String toString() {
		return "AddonInfo [id=" + id + ", jarPath=" + jarPath + ", addonClass=" + addonClass + ", title=" + title
				+ ", description=" + description + ", author=" + author + ", authorWebsite=" + authorWebsite
				+ ", version=" + version + ", versionCode=" + versionCode + ", updateUrl=" + updateUrl + ", hasError="
				+ hasError + ", start=" + start + ", addon=" + addon + ", hooks=" + hooks + "]";
	}
	
	
}
