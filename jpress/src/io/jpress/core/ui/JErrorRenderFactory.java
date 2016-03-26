package io.jpress.core.ui;

import io.jpress.core.Jpress;
import io.jpress.template.TemplateUtils;

import com.jfinal.kit.PathKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.IErrorRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.TextRender;

public class JErrorRenderFactory extends FreeMarkerRender implements IErrorRenderFactory {
	
	private int errorCode;
	
	public JErrorRenderFactory() {
		super(null);// FreeMarkerRender view,init view in render() method
	}

	@Override
	public Render getRender(int errorCode, String view) {
		this.errorCode = errorCode;
		return this;
	}

	@Override
	public void render() {
		request.setAttribute("errorCode", errorCode);
		
		if(!Jpress.isInstalled()){
			renderText("404 error in jpress.");
			return;
		}
		
		String templateName = TemplateUtils.getTemplateName();
		if(null == templateName){
			renderText(String.format("%s error!you haven't configure your template yet.", errorCode));
			return;
		}
		
		if(errorCode > 500) 
			errorCode = 500;
		
		String htmlName = errorCode+".html";
		
		String viewPath = String.format("/templates/%s/%s",templateName,htmlName);
		
		if(!TemplateUtils.exists(PathKit.getWebRootPath()+viewPath)){
			renderText(String.format("%s error! there is no \"%s\" file in template \"%s\".",errorCode,htmlName,templateName));
		}else{
			this.view = viewPath;
			super.render();
		}
		
	}

	private void renderText(String text) {
		new TextRender(text).setContext(request, response).render();
	}
	

}
