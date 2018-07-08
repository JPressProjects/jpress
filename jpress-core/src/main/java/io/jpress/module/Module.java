package io.jpress.module;

import io.jpress.core.menu.AdminMenuGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class Module {

    private List<AdminMenuGroup> menuGroups;

    public void addMenuGroup(String id, String text, String icon, int order) {
        if (menuGroups == null) {
            menuGroups = new ArrayList<>();
        }

        AdminMenuGroup menuGroup = new AdminMenuGroup();
        menuGroup.setText(text);
        menuGroup.setIcon(icon);
        menuGroup.setId(id);
        menuGroup.setOrder(order);

        menuGroups.add(menuGroup);
    }

    public List<AdminMenuGroup> getMenuGroups() {
        return menuGroups;
    }

}
