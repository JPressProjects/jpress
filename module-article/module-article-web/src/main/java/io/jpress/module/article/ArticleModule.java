package io.jpress.module.article;

import io.jpress.module.Module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page
 */
public class ArticleModule {

    public static final Module articleModule = new Module();

    static {
        articleModule.addMenuGroup("article", "文章", "", 1);
    }
}
