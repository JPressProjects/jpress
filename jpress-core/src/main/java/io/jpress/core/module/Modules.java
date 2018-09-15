package io.jpress.core.module;

import java.util.LinkedList;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class Modules {

    public LinkedList<Module> list = new LinkedList<>();

    public void add(Module module) {
        list.add(module);
    }

    public void add(Module module, int toIndex) {
        list.add(toIndex, module);
    }


    public LinkedList<Module> getList() {
        return list;
    }

}
