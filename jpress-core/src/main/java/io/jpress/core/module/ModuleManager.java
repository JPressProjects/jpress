package io.jpress.core.module;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.module
 */
public class ModuleManager {
    private static final ModuleManager me = new ModuleManager();
    private Modules modules = new Modules();

    private ModuleManager() {

    }

    public static ModuleManager me() {
        return me;
    }

    public Modules getModules() {
        return modules;
    }

}
