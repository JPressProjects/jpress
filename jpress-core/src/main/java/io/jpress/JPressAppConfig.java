package io.jpress;

import io.jboot.utils.ClassKits;
import io.jboot.utils.ClassScanner;
import io.jpress.core.menu.AdminMenuManager;
import io.jpress.module.Modules;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 核心配置
 * @Package io.jpress
 */
class JPressAppConfig {

    private static final JPressAppConfig CONFIG = new JPressAppConfig();
    private static final Modules MODULES = new Modules();

    private static final List<JPressAppListener> APP_LISTENERS = new ArrayList<>();

    public static JPressAppConfig me() {
        return CONFIG;
    }

    public void init() {

        initJPressAppListeners();
        invokeJPressAppListeners();

        initAdminMenus();
    }

    /**
     * 初始化 监听器
     * 其他 module 会在监听器里完成所有需要的配置
     */
    private void initJPressAppListeners() {

        List<Class<JPressAppListener>> classes = ClassScanner.scanSubClass(JPressAppListener.class, true);
        if (classes == null) {
            return;
        }

        for (Class<JPressAppListener> jPressAppListenerClass : classes) {
            APP_LISTENERS.add(ClassKits.newInstance(jPressAppListenerClass));
        }
    }

    private void invokeJPressAppListeners() {
        for (JPressAppListener listener : APP_LISTENERS) {
            listener.onConfigModule(MODULES);
        }
    }


    /**
     * 初始化 后台菜单
     */
    private void initAdminMenus() {

        AdminMenuManager.me().init(MODULES);

    }


}
