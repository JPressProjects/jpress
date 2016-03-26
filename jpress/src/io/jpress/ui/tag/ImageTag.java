package io.jpress.ui.tag;

import io.jpress.core.Jpress;
import io.jpress.core.ui.JTag;
import io.jpress.template.Thumbnail;

public class ImageTag extends JTag {

	@Override
	public void onRender() {

		String name = getParam("name");
		String style = getParam("style");
		String src = getParam("src");

		Thumbnail tb = Jpress.currentTemplate().getThumbnailByName(name);
		
		int inserTo = src.lastIndexOf(".");
		
		String newPath = src.substring(0, inserTo) + "_"
				+ tb.getSizeAsString() + src.substring(inserTo, src.length());

		String imageTag = String.format("<img src=\"%s\" style=\"%s\" />", newPath, style);
		
		renderText(imageTag);

	}

}
