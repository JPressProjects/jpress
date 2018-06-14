package io.jpress.module.page;

import io.jpress.module.Module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.module.page
 */
public class PageModule {
    public static final Module pageModule = new Module();

    static {
        pageModule.addMenuGroup("page", "页面", "", 99);
    }
}
