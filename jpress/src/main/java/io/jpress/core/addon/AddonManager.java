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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

/**
 * 插件管理器，负责加载、启动、停止插件。
 * 
 * @author michael
 */
public class AddonManager {

	private static final Log log = Log.getLog(AddonManager.class);

	private List<Addon> addonList = new ArrayList<Addon>();
	private static AddonManager manager = new AddonManager();

	private AddonManager() {
		addonList.clear();
		load();
	}

	public void reload() {
		addonList.clear();
		load();
	}

	public static AddonManager get() {
		return manager;
	}

	public void start() {

	}

	public void start(Addon addon) {

	}

	public void stop(Addon addon) {

	}

	public List<Addon> getAddons() {
		return addonList;
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
		Addon addon = new Addon();
		try {
			JarFile jarFile = new JarFile(file);
			Manifest mf = jarFile.getManifest();
			Attributes attr = mf.getMainAttributes();
			if (attr != null) {

				String className = attr.getValue("Addon-Class");
				String title = attr.getValue("Addon-Title");
				String description = attr.getValue("Addon-Description");
				String author = attr.getValue("Addon-Author");
				String authorWebsite = attr.getValue("Addon-Author-Website");
				String version = attr.getValue("Addon-Version");
				String versionCode = attr.getValue("Addon-Version-Code");

				addon.setTitle(title);
				addon.setAddonClass(className);
				addon.setDescription(description);
				addon.setAuthor(author);
				addon.setAuthorWebsite(authorWebsite);
				addon.setVersion(version);
				addon.setVersionCode(Integer.parseInt(versionCode.trim()));

				AddonClassLoader acl = new AddonClassLoader(file.getAbsolutePath());
				acl.init();
				@SuppressWarnings("unchecked")
				Class<? extends IAddon> clazz = (Class<? extends IAddon>) acl.loadClass(className);
				addon.setAddonImpl(clazz.newInstance());

				return addon;
			}
		} catch (Throwable e) {
			addon.setHasError(true);
			log.error("AddonManager loadAddon error", e);
		}
		return addon;
	}
	
	
	public Object invokeHook(String hookName, Object... objects) {
		List<Addon> addons = getStartedAddons();
		for (Addon addon : addons) {
			if (addon.getHasError()) {
				continue;
			}
			Method method = addon.getHooks().method(hookName);
			if (method != null) {
				Hook hook = null;
				try {
					hook = addon.getHooks().hook(hookName);
					Object ret = method.invoke(hook, objects);
					if (!hook.letNextHookInvoke()) {
						return ret;
					}
				} catch (Throwable e) {
					addon.setHasError(true);
					log.error("HookInvoker invoke error", e);
				} finally {
					if (hook != null) {
						hook.hookInvokeFinished();
					}
				}
			}
		}
		return null;
	}

}
