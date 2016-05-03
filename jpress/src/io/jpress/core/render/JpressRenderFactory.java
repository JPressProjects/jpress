package io.jpress.core.render;

import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;

import io.jpress.core.Jpress;

public class JpressRenderFactory implements IMainRenderFactory {

	public JpressRenderFactory() {
	}

	@Override
	public Render getRender(String view) {
		// front url
		if (view.startsWith("/templates")) {
			String renderType = Jpress.currentTemplate().getRenderType();
			if ("thymeleaf".equalsIgnoreCase(renderType)) {
				return new ThymeleafRender(view);
			} else if ("freemarker".equalsIgnoreCase(renderType)) {
				return new JFreemarkerRender(view);
			}
		}

		//admin url
		return new JFreemarkerRender(view);
	}

	@Override
	public String getViewExtension() {
		return ".html";
	}

}
