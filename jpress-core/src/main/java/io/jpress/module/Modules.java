package io.jpress.module;

import java.util.LinkedList;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class Modules {

    public LinkedList<Module> modules = new LinkedList<>();

    public void add(Module module) {
        modules.add(module);
    }

    public void add(Module module, int toIndex) {
        modules.add(toIndex, module);
    }


    public LinkedList<Module> getModules() {
        return modules;
    }

}
