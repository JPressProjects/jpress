package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;

import com.jfinal.plugin.activerecord.Page;

public class CommentTag extends JTag {

	@Override
	public void onRender() {
		
		
		Page<Content> page = Content.DAO.doPaginate(1, 10);
		
		setVariable("comments", page.getList());
		
		renderBody();
	}

}
