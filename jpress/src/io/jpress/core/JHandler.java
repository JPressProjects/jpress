package io.jpress.core;

import io.jpress.model.Option;
import io.jpress.plugin.target.TargetKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;

public class JHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,HttpServletResponse response, boolean[] isHandled) {

		long time = System.currentTimeMillis();

		String contextPath = request.getContextPath();
		request.setAttribute("CPATH", contextPath);
		request.setAttribute("SPATH", contextPath + "/static");

		if (target.indexOf('.') != -1) {
			// 防止直接访问模板文件
			if (target.startsWith("/templates") && target.endsWith("html")) {
				HandlerKit.renderError404(request, response, isHandled);
			}
			
			if("sitemap.xml".equalsIgnoreCase(target)){
				target = "sitemap";
			}
			
			return;
		}
		
		// 检测是否安装
		if (!Jpress.isInstalled() && !target.startsWith("/install")) {
			HandlerKit.redirect(contextPath + "/install", request, response,isHandled);
			return;
		}

		if(Jpress.isInstalled()){
			request.setAttribute("TPATH",contextPath + "/templates/" + Option.findTemplateName());
			Boolean cdnEnable = Option.findValueAsBool("cdn_enable");
			if(cdnEnable != null && cdnEnable){
				String cdnDomain = Option.cacheValue("cdn_domain");
				if(cdnDomain!=null && !"".equals(cdnDomain.trim())){
					request.setAttribute("CDN", cdnDomain);
				}
			}
		}
		
		target = targetConvert(target, request, response);
		next.handle(target, request, response, isHandled);

		if (Jpress.isDevMode()) {
			System.err.println("--->time:" + (System.currentTimeMillis() - time));
		}

	}

	private String targetConvert(String target, HttpServletRequest request,HttpServletResponse response) {
		return TargetKit.converte(target, request, response);
	}

}
