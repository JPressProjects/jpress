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
import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.render.FreeMarkerRender;

import io.jpress.core.render.freemarker.JFunction;
import io.jpress.core.render.freemarker.JTag;

public class Jpress {

	public static final String VERSION = "0.5.0";

	public static final Map<String, Object> jpressTags = new HashMap<String, Object>();

	public static void addTag(String key, JTag tag) {
		if (key.startsWith("jp.")) {
			key = key.substring(3);
		}
		jpressTags.put(key, tag);
	}

	public static void addFunction(String key, JFunction function) {
		FreeMarkerRender.getConfiguration().setSharedVariable(key, function);
	}

	public static void renderImmediately() {
		FreeMarkerRender.getConfiguration().setTemplateUpdateDelayMilliseconds(0);
	}

	private static boolean isInstalled = false;

	public static boolean isInstalled() {
		if (!isInstalled) {
			File dbConfig = new File(PathKit.getRootClassPath(), "db.properties");
			isInstalled = dbConfig.exists();
		}
		return isInstalled;
	}

	public static boolean isDevMode() {
		return JFinal.me().getConstants().getDevMode();
	}

	private static boolean isLoaded = false;

	public static boolean isLoaded() {
		return isLoaded;
	}

	public static void loadFinished() {
		isLoaded = true;
	}

}
