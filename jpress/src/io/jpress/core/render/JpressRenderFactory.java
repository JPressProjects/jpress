package io.jpress.core.render;

import com.jfinal.kit.PropKit;
import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;

public class JpressRenderFactory implements IMainRenderFactory {

	@Override
	public Render getRender(String view) {
		if ("freemarker".equalsIgnoreCase(PropKit.get("render"))) {
			return new JFreemarkerRender(view);
		} else if ("thymeleaf".equalsIgnoreCase(PropKit.get("render"))) {
			return new ThymeleafRender(view);
		}
		return new JFreemarkerRender(view);
	}

	@Override
	public String getViewExtension() {
		return ".html";
	}

}
