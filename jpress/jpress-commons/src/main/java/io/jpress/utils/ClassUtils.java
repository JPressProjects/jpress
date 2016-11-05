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
package io.jpress.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

public class ClassUtils {

	private static final Log log = Log.getLog(ClassUtils.class);

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
		return scanSubClass(pclazz, false);
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean mustbeCanNewInstance) {
		if (pclazz == null) {
			log.error("scanClass: parent clazz is null");
			return null;
		}

		List<File> classFileList = new ArrayList<File>();
		scanClass(classFileList, PathKit.getRootClassPath());

		List<Class<T>> classList = new ArrayList<Class<T>>();
		for (File file : classFileList) {

			int start = PathKit.getRootClassPath().length();
			int end = file.toString().length() - 6; // 6 == ".class".length();

			String classFile = file.toString().substring(start + 1, end);
			Class<T> clazz = classForName(classFile.replace(File.separator, "."));

			if (clazz != null && pclazz.isAssignableFrom(clazz)) {
				if (mustbeCanNewInstance) {
					if (clazz.isInterface())
						continue;

					if (Modifier.isAbstract(clazz.getModifiers()))
						continue;
				}
				classList.add(clazz);
			}
		}

		File jarsDir = new File(PathKit.getWebRootPath() + "/WEB-INF/lib");
		if (jarsDir.exists() && jarsDir.isDirectory()) {
			File[] jarFiles = jarsDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName().toLowerCase();
					return name.endsWith(".jar") && name.startsWith("jpress");
				}
			});

			if (jarFiles != null && jarFiles.length > 0) {
				for (File f : jarFiles) {
					classList.addAll(scanSubClass(pclazz, f, mustbeCanNewInstance));
				}
			}
		}

		return classList;
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, File f, boolean mustbeCanNewInstance) {
		if (pclazz == null) {
			log.error("scanClass: parent clazz is null");
			return null;
		}

		JarFile jarFile = null;

		try {
			jarFile = new JarFile(f);
			List<Class<T>> classList = new ArrayList<Class<T>>();
			Enumeration<JarEntry> entries = jarFile.entries();

			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String entryName = jarEntry.getName();
				if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
					String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
					Class<T> clazz = classForName(className);
					if (clazz != null && pclazz.isAssignableFrom(clazz)) {
						if (mustbeCanNewInstance) {
							if (clazz.isInterface())
								continue;

							if (Modifier.isAbstract(clazz.getModifiers()))
								continue;
						}
						classList.add(clazz);
					}
				}
			}

			return classList;

		} catch (IOException e1) {
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
				}
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> classForName(String className) {
		Class<T> clazz = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			clazz = (Class<T>) Class.forName(className, false, cl);
		} catch (Throwable e) {
			log.error("classForName is error，className:" + className);
		}
		return clazz;
	}

	private static void scanClass(List<File> fileList, String path) {
		File files[] = new File(path).listFiles();
		if (null == files || files.length == 0)
			return;
		for (File file : files) {
			if (file.isDirectory()) {
				scanClass(fileList, file.getAbsolutePath());
			} else if (file.getName().endsWith(".class")) {
				fileList.add(file);
			}
		}
	}

}
