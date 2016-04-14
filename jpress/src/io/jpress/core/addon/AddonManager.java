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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.jfinal.kit.PathKit;

public class AddonManager {

	List<Addon> addonList = new ArrayList<Addon>();

	public void init() {
		addonList.clear();
		load();
	}

	public void reload() {
		addonList.clear();
		load();
	}

	public List<Addon> getStartedAddons() {
		return addonList;
	}

	private void load() {
		File addonsFile = new File(PathKit.getWebRootPath(), "/WEB-INF/addons");
		if (addonsFile.exists()) {
			File[] files = addonsFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") || name.endsWith(".zip");
				}
			});

			if (files != null && files.length > 0) {
				for (File file : files) {
					Addon addon = loadAddon(file);
					if (addon != null) {
						addon.start();
						addonList.add(addon);
					}
				}
			}
		}
	}

	private static Addon loadAddon(File file) {
		try {
			JarFile jarFile = new JarFile(file);
			Manifest mf = jarFile.getManifest();
			Attributes attr = mf.getMainAttributes();
			if (attr != null) {
				Addon addon = new Addon();
				String className = attr.getValue("Addon-Main-Class");

				AddonClassLoader acl = new AddonClassLoader(file.getAbsolutePath());
				acl.init();
				@SuppressWarnings("unchecked")
				Class<? extends IAddon> clazz = (Class<? extends IAddon>) acl.loadClass(className);
				addon.setAddonImpl(clazz.newInstance());
				addon.setTitle("test");
				return addon;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
