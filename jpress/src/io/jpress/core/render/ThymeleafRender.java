package io.jpress.core.render;

import com.jfinal.render.Render;

public class ThymeleafRender extends Render{

	private static final String contentType = "text/html; charset=" + getEncoding();
	private String view;
	
	public ThymeleafRender(String view) {
		this.view = view;
	}
	
	@Override
	public void render() {
		
		
	}

}
