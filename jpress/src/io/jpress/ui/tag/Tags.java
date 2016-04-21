package io.jpress.ui.tag;

import com.jfinal.aop.Invocation;

import io.jpress.core.Jpress;
import io.jpress.core.ui.JWidgetContainer;

public class Tags {

	public static void initInStarted() {
		Jpress.addTag("jp_content", new ContentTag());
		Jpress.addTag("jp_contents", new ContentsTag());

		Jpress.addTag("jp_comment", new CommentTag());
		Jpress.addTag("jp_comments", new CommentsTag());
		Jpress.addTag("jp_comment_page", new CommentPageTag());

		Jpress.addTag("jp_menu", new MenuTag());
		Jpress.addTag("jp_module", new ModuleTag());
		Jpress.addTag("jp_widgets", new JWidgetContainer());
	}

	public static void initInInterceptor(Invocation invocation) {
		Jpress.addTag("jp_content_page", new ContentPageTag(invocation.getController()));
	}

}
