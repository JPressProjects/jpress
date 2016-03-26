package io.jpress.template;

import io.jpress.model.Option;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;

public class TemplateUtils {

	private static Map<String, Boolean> cache = new ConcurrentHashMap<String, Boolean>();
	public static boolean exists(String path) {
		Boolean result = cache.get(path);
		if (null == result || !result) {
			result = new File(path).exists();
			cache.put(path, result);
		}
		return result;
	}
	
	public static boolean existsFile(String fileName) {
		String templateName = TemplateUtils.getTemplateName();
		if(null == templateName){
			return false;
		}
		
		String viewPath = String.format("/templates/%s/%s",templateName,fileName);
		return exists(PathKit.getWebRootPath()+viewPath);
	}

	public static String getTemplateName() {
		String templateName = Option.findTemplateName();

		if (null != templateName && !"".equals(templateName.trim())) {
			return templateName;
		}

		if (JFinal.me().getConstants().getDevMode()) {
			return "default";
		} else {
			return null;
		}
	}
	
	public static String getTemplatePath(){
		return String.format("%s/templates/%s", PathKit.getWebRootPath(),getTemplateName());
	}

}
