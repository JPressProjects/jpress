package io.jpress.module.article;

import io.jpress.core.module.Module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page
 */
public class ArticleModule extends Module {

    private String id = "article";
    private String text = "文章";

    private ArticleModule() {
        addMenu(id, text, "<i class=\"fa fa-fw fa-file-text\"></i>", 1);
    }

    private static final ArticleModule me = new ArticleModule();

    public static ArticleModule me() {
        return me;
    }

    @Override
    public String onGetDashboardBoxHtml() {
        return "article/_article_dashboard_box.html";
    }
}
