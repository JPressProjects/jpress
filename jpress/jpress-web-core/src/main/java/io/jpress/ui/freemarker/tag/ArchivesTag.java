package io.jpress.ui.freemarker.tag;

import java.util.List;

import io.jpress.Consts;
import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.vo.Archive;
import io.jpress.utils.StringUtils;

public class ArchivesTag extends JTag {
	
	public static final String TAG_NAME = "jp.archives";

	@Override
	public void onRender() {

		String module = getParam("module", Consts.MODULE_ARTICLE);
		if (StringUtils.isBlank(module)) {
			renderText("");
			return;
		}

		List<Archive> list = ContentQuery.me().findArchives(module);
		if (list == null || list.isEmpty()) {
			renderText("");
			return;
		}

		List<Content> contents = ContentQuery.me().findArchiveByModule(module);
		if (contents == null || contents.isEmpty()) {
			renderText("");
			return;
		}

		for (Content c : contents) {
			String archiveDate = c.getStr("archiveDate");
			if (archiveDate == null)
				continue;
			for (Archive a : list) {
				if (archiveDate.equals(a.getDate())) {
					a.addData(c);
				}
			}
		}

		setVariable("archives", list);
		renderBody();
	}

}
