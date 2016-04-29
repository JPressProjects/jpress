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
package io.jpress.core;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

public class ClassScaner {
	
	private static final Log logger = Log.getLog(ClassScaner.class);

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
		return scanSubClass(pclazz, false);
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean mustbeCanNewInstance) {
		if (pclazz == null) {
			logger.error("scanClass: parent clazz is null");
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

		return classList;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> classForName(String className) {
		Class<T> clazz = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			clazz = (Class<T>) Class.forName(className, false, cl);
		} catch (Exception e) {
			logger.error("classForName is error", e);
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
