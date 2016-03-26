package io.jpress.ui.widget;

import io.jpress.core.ui.JWidget;

public class CCPostWidget extends JWidget {
	
	public CCPostWidget() {
		super();
		put("aa1", "bb1");
		put("aa2", "bb2");
		put("aa3", "bb3");
		put("aa4", "bb4");
	}

	@Override
	public String onRenderHtmlForSetting(WidgetConfig config) {

		return null;
	}

	@Override
	public String onRenderHtml(WidgetConfig config) {
		
		return null;
	}


}
