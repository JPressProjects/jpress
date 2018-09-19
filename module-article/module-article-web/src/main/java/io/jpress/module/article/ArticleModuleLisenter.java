package io.jpress.module.article;

import io.jpress.core.module.JPressModuleListener;
import io.jpress.core.module.Modules;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Module 监听器
 * @Description: 每个 module 都应该有这样的一个监听器，用来配置自身Module的信息，比如后台菜单等
 * @Package io.jpress.module.page
 */
public class ArticleModuleLisenter implements JPressModuleListener {


    @Override
    public void onConfigModule(Modules modules) {
        modules.add(ArticleModule.me());
    }
}
