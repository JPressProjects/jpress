package io.jpress.core.render;

import com.jfinal.render.Render;

public class ThymeleafRender extends Render {
	
	//http://stackoverflow.com/questions/27277046/thymeleaf-dont-evaluate-custom-dialects-processors

	//demo:https://github.com/mxab/thymeleaf-extras-data-attribute
	private static final String contentType = "text/html; charset=" + getEncoding();
	private String view;

	public ThymeleafRender(String view) {
		this.view = view;
	}

	@Override
	public void render() {
		throw new RuntimeException("ThymeleafRender not finished!");
	}

}
