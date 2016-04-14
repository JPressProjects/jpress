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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AddonClassLoader extends URLClassLoader {

	private String path;

	public AddonClassLoader(String path) {
		super(new URL[] {}, null);
		this.path = path;
	}

	public void load() {
		File jarFile = new File(path);
		try {
			addURL(jarFile.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Addon getAddon() {
		try {
			JarFile jarFile = new JarFile(new File(path));
			Manifest mf = jarFile.getManifest();
			Attributes attr = mf.getAttributes("Addon");
			if (attr != null) {
				String className = attr.getValue("main-class");
				@SuppressWarnings("unchecked")
				Class<? extends Addon> clazz = (Class<? extends Addon>) this.loadClass(className);
				Addon addon = clazz.newInstance();
				
				addon.setTitle("test");

				return addon;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
