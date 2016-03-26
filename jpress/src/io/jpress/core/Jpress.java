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

import io.jpress.template.ConfigParser;
import io.jpress.template.Template;
import io.jpress.template.TemplateUtils;

import java.io.File;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.render.FreeMarkerRender;

import freemarker.template.TemplateModelException;

public class Jpress {

	private static final Log logger = Log.getLog(Jpress.class);

	public static void start() {
		start(8080);
	}

	public static void start(int port) {
		JFinal.start("WebRoot", port, "/", 5);
	}

	public static void setFreeMarkerSharedVariable(String key, Object value) {
		try {
			FreeMarkerRender.getConfiguration().setSharedVariable(key, value);
		} catch (TemplateModelException e) {
			logger.error("setFreeMarkerSharedVariable", e);
		}
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
