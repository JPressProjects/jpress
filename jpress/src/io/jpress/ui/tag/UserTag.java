package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;

import com.jfinal.plugin.activerecord.Page;

public class UserTag extends JTag {

	@Override
	public void onRender() {
		
		int id = getParamToInt("id",0);
		System.out.println("cccid--->"+id);
		
		
		Page<Content> page = Content.DAO.doPaginate(1, 10);
		
		setVariable("contentList", page.getList());
		
		renderBody();
	}

}
