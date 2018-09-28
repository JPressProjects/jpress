package io.jpress.module.page;

import io.jpress.core.module.Module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page
 */
public class PageModule extends Module {

    private String id = "page";
    private String text = "页面";

    private PageModule() {
        addAdminMenu(id, text, "<i class=\"fa fa-fw fa-file\"></i>", 2);
    }

    private static final PageModule me = new PageModule();

    public static PageModule me() {
        return me;
    }
}
