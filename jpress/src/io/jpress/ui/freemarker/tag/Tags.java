package io.jpress.ui.freemarker.tag;


import io.jpress.core.Jpress;

public class Tags {

	public static void init() {
		Jpress.addTag("jp_content", new ContentTag());
		Jpress.addTag("jp_contents", new ContentsTag());

		Jpress.addTag("jp_comment", new CommentTag());
		Jpress.addTag("jp_comments", new CommentsTag());
		Jpress.addTag("jp_comment_page", new CommentPageTag());

		Jpress.addTag("jp_menu", new MenuTag());
		Jpress.addTag("jp_module", new ModuleTag());
		Jpress.addTag("jp_tags", new TagsTag());
	}


}
