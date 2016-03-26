package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Taxonomy;

import com.jfinal.plugin.activerecord.Page;

public class TagsTag extends JTag {

	@Override
	public void onRender() {
		
		int count = getParamToInt("count",0);
		count = count <= 0 ? 10 :count;
		
		Page<Taxonomy> page = Taxonomy.DAO.doPaginate(1, count, "tag");
		setVariable("tags", page.getList());
		renderBody();
	}

}
