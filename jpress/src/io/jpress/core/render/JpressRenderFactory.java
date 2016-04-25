package io.jpress.core.render;

import com.jfinal.kit.PropKit;
import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;

public class JpressRenderFactory implements IMainRenderFactory {

	private static final int RENDER_TYPE_FREEMARKER = 1;
	private static final int RENDER_TYPE_THYMELEAF = 2;

	private int render_type = 0;

	public JpressRenderFactory() {
		if ("freemarker".equalsIgnoreCase(PropKit.get("render"))) {
			render_type = RENDER_TYPE_FREEMARKER;
		} else if ("thymeleaf".equalsIgnoreCase(PropKit.get("render"))) {
			render_type = RENDER_TYPE_THYMELEAF;
		}
	}

	@Override
	public Render getRender(String view) {

		switch (render_type) {
		case RENDER_TYPE_FREEMARKER:
			return new JFreemarkerRender(view);

		case RENDER_TYPE_THYMELEAF:
			return new ThymeleafRender(view);

		}

		return new JFreemarkerRender(view);
	}

	@Override
	public String getViewExtension() {
		return ".html";
	}

}
