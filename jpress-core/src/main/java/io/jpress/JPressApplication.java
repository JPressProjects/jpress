package io.jpress;

import io.jboot.utils.ClassKits;
import io.jboot.utils.ClassScanner;
import io.jpress.core.menu.AdminMenuManager;
import io.jpress.core.module.ModuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 核心配置
 * @Package io.jpress
 */
public class JPressApplication {

    private static final JPressApplication CONFIG = new JPressApplication();
    private static final List<JPressAppListener> APP_LISTENERS = new ArrayList<>();

    public static JPressApplication me() {
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
            listener.onConfigModule(ModuleManager.me().getModules());
        }
    }


    /**
     * 初始化 后台菜单
     */
    private void initAdminMenus() {

        AdminMenuManager.me().init();

    }


}
