package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;

import java.util.List;

public class MenuTag extends JTag {

	@Override
	public void onRender() {

//		String menuName = getParam("name");

		List<Content> list = Content.DAO.findMenuList();
		ModelSorter.tree(list);
		setVariable("menus", list);
		renderBody();
	}

}
