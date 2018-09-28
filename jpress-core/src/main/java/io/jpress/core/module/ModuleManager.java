package io.jpress.core.module;

import io.jboot.utils.ClassKits;
import io.jboot.utils.ClassScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.module
 */
public class ModuleManager {
    private static final ModuleManager me = new ModuleManager();
//    private Modules modules = new Modules();
    private  List<ModuleListener> moduleListeners = new ArrayList<>();


    private ModuleManager() {
        initModuleListeners();
    }

    public static ModuleManager me() {
        return me;
    }

//    public Modules getModules() {
//        return modules;
//    }


//    public void init() {
//        initModuleListeners();
////        invokeModuleListeners();
//    }


    public  List<ModuleListener> getListeners() {
        return moduleListeners;
    }

    /**
     * 初始化 监听器
     * 其他 module 会在监听器里完成所有需要的配置
     */
    private void initModuleListeners() {

        List<Class<ModuleListener>> classes = ClassScanner.scanSubClass(ModuleListener.class, true);
        if (classes == null) {
            return;
        }

        for (Class<ModuleListener> jPressAppListenerClass : classes) {
            moduleListeners.add(ClassKits.newInstance(jPressAppListenerClass));
        }
    }

//    private void invokeModuleListeners() {
//        for (ModuleListener listener : moduleListeners) {
//            listener.onConfigModule(ModuleManager.me().getModules());
//        }
//    }

}
