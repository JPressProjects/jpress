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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.jfinal.log.Log;

public class AddonClassLoader extends URLClassLoader {
	private static final Log log = Log.getLog(AddonClassLoader.class);
	private String path;

	public AddonClassLoader(String path) {
		super(new URL[] {}, Thread.currentThread().getContextClassLoader());
		this.path = path;
	}

	public void init() {
		File jarFile = new File(path);
		try {
			addURL(jarFile.toURI().toURL());
		} catch (MalformedURLException e) {
			log.error("AddonClassLoader init error", e);
		}
	}

	public void autoLoadClass(JarFile jarfile) {
		Enumeration<JarEntry> entries = jarfile.entries();

		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String entryName = jarEntry.getName();
			if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
				String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
				try {
					loadClass(className);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
