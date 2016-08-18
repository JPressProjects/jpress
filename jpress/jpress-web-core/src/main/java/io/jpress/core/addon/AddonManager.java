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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import io.jpress.model.query.OptionQuery;
import io.jpress.plugin.message.MessageKit;
import io.jpress.utils.FileUtils;
import io.jpress.utils.StringUtils;

/**
 * 插件管理器，负责加载、启动、停止插件。
 * 
 * @author michael
 */
public class AddonManager {

	public static final String MESSAGE_ON_ADDON_LOAD_START = "addonManager:on_addon_load_start";
	public static final String MESSAGE_ON_ADDON_LOAD_FINISHED = "addonManager:on_addon_load_finished";

	private static final Log log = Log.getLog(AddonManager.class);

	private final Map<String, AddonInfo> addonMap = new ConcurrentHashMap<String, AddonInfo>();
	private final Map<String, AddonInfo> startedAddonMap = new ConcurrentHashMap<String, AddonInfo>();
	private static AddonManager manager = new AddonManager();

	private AddonManager() {
		addonMap.clear();
		startedAddonMap.clear();
		loadAllAddons();
	}

	public void reload() {
		addonMap.clear();
		startedAddonMap.clear();
		loadAllAddons();
	}

	public static AddonManager get() {
		return manager;
	}

	public AddonInfo findById(String id) {
		if (id == null) {
			return null;
		}

		return addonMap.get(id);
	}

	public boolean start(AddonInfo addon) {
		OptionQuery.me().saveOrUpdate(buildOptionKey(addon), Boolean.TRUE.toString());
		boolean isSuccess = addon.start();
		if (!isSuccess) {
			log.warn("addon:" + addon.getId() + " start fail!!!");
		} else {
			startedAddonMap.put(addon.getId(), addon);
		}
		return isSuccess;
	}

	private String buildOptionKey(AddonInfo addon) {
		return "addon_start_" + addon.getId();
	}

	public boolean stop(AddonInfo addon) {
		OptionQuery.me().saveOrUpdate(buildOptionKey(addon), Boolean.FALSE.toString());
		boolean isSuccess = addon.stop();
		if (!isSuccess) {
			log.warn("addon:" + addon.getId() + " stop fail!!!");
		} else {
			startedAddonMap.remove(addon);
		}
		return isSuccess;
	}

	public boolean uninstall(AddonInfo addon) {

		boolean isStoped = stop(addon);
		if (!isStoped) {
			return false;
		}

		File addonJarFile = new File(PathKit.getWebRootPath(), addon.getJarPath());
		if (addonJarFile.exists()) {
			unRegisterAddon(addon);
			return addonJarFile.delete();
		}
		return false;
	}

	public List<AddonInfo> getAddons() {
		List<AddonInfo> addonList1 = new ArrayList<AddonInfo>();
		for (Map.Entry<String, AddonInfo> entry : addonMap.entrySet()) {
			addonList1.add(entry.getValue());
		}
		return addonList1;
	}

	public List<AddonInfo> getStartedAddons() {
		List<AddonInfo> startedAddonList1 = new ArrayList<AddonInfo>();
		for (Map.Entry<String, AddonInfo> entry : startedAddonMap.entrySet()) {
			startedAddonList1.add(entry.getValue());
		}
		return startedAddonList1;
	}

	public void registerAddon(AddonInfo addon) {
		addonMap.put(addon.getId(), addon);
	}

	public void unRegisterAddon(AddonInfo addon) {
		addonMap.remove(addon.getId());
	}

	private void loadAllAddons() {
		MessageKit.sendMessage(MESSAGE_ON_ADDON_LOAD_START, this);

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

					AddonInfo addon = loadAddonByJarFile(file);
					if (addon == null) {
						continue;
					}

					registerAddon(addon);

					Boolean start = OptionQuery.me().findValueAsBool(buildOptionKey(addon));
					if (start != null && start == true) {
						start(addon);
					}
				}
			}
		}

		MessageKit.sendMessage(MESSAGE_ON_ADDON_LOAD_FINISHED, this);
	}

	@SuppressWarnings({ "unchecked" })
	private static AddonInfo loadAddonByJarFile(File file) {
		AddonInfo addon = null;
		JarFile jarFile = null;
		AddonClassLoader acl = null;
		try {
			jarFile = new JarFile(file);
			acl = new AddonClassLoader(file.getAbsolutePath());
			acl.init();

			Manifest mf = jarFile.getManifest();
			Attributes attr = mf.getMainAttributes();
			if (attr != null) {

				String id = attr.getValue("Addon-Id");
				if (StringUtils.isBlank(id)) {
					log.warn("addon " + file.getParentFile() + " must has id");
					return null;
				}

				String className = attr.getValue("Addon-Class");
				String title = attr.getValue("Addon-Title");
				String description = attr.getValue("Addon-Description");
				String author = attr.getValue("Addon-Author");
				String authorWebsite = attr.getValue("Addon-Author-Website");
				String version = attr.getValue("Addon-Version");
				String versionCode = attr.getValue("Addon-Version-Code");

				addon = new AddonInfo();
				addon.setId(id);
				addon.setTitle(title);
				addon.setAddonClass(className);
				addon.setDescription(description);
				addon.setAuthor(author);
				addon.setAuthorWebsite(authorWebsite);
				addon.setVersion(version);
				addon.setVersionCode(Integer.parseInt(versionCode.trim()));
				addon.setJarPath(FileUtils.removeRootPath(file.getAbsolutePath()));

				Class<? extends IAddon> clazz = (Class<? extends IAddon>) acl.loadClass(className);
				addon.setAddon(clazz.newInstance());
				return addon;
			}
		} catch (Throwable e) {
			addon.setHasError(true);
			log.error("AddonManager loadAddon error", e);
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			if (acl != null) {
				try {
					acl.close();
				} catch (IOException e) {
				}
			}
		}
		return addon;
	}

	public Object invokeHook(String hookName, Object... objects) {
		List<AddonInfo> addons = getStartedAddons();
		for (AddonInfo addon : addons) {
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
					log.error("invokeHook  error , hook name:+" + hookName + " \r\n + error addon:\r\n" + addon, e);
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
