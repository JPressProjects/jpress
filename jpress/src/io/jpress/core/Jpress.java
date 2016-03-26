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
