package io.jpress.module.article;

import io.jpress.JPressAppListener;
import io.jpress.module.Modules;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.module.page
 */
public class AppLisenter implements JPressAppListener {


    @Override
    public void onConfigModule(Modules modules) {
        modules.add(ArticleModule.articleModule);
    }
}
