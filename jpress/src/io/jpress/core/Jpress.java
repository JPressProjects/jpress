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

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.render.FreeMarkerRender;

import io.jpress.core.ui.JFunction;
import io.jpress.core.ui.JTag;
import io.jpress.template.ConfigParser;
import io.jpress.template.Template;
import io.jpress.template.TemplateUtils;

public class Jpress {

	public static void start() {
		start(8080);
	}

	public static void start(int port) {
		JFinal.start("WebRoot", port, "/", 5);
	}

	public static void addTag(String key,JTag tag) {
		FreeMarkerRender.getConfiguration().setSharedVariable(key, tag);
	}
	
	public static void addFunction(String key, JFunction function) {
		FreeMarkerRender.getConfiguration().setSharedVariable(key, function);
	}

	private static boolean isInstalled = false;
	public static boolean isInstalled() {
		if (!isInstalled){
			File dbConfig = new File(PathKit.getRootClassPath(), "db.properties");
			isInstalled = dbConfig.exists();
		}
		return isInstalled;
	}
	
	private static Template cTemplate;
	public static Template currentTemplate(){
		if(cTemplate == null){
			String tName = TemplateUtils.getTemplateName();
			cTemplate = new ConfigParser().parser(tName);
		}
		return cTemplate;
	}
	
	public static void templateChanged(){
		cTemplate = null;
	}
	
	public static boolean isDevMode(){
		return JFinal.me().getConstants().getDevMode();
	}

}
