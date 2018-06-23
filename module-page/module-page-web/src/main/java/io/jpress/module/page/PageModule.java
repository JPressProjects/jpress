package io.jpress.module.page;

import io.jpress.module.Module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page
 */
public class PageModule {

    public static final Module pageModule = new Module();

    static {
        pageModule.addMenuGroup("page", "页面", "", 99);
    }
}
