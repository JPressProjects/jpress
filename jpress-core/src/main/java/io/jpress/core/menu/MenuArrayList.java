package io.jpress.core.menu;

import java.util.ArrayList;


public class MenuArrayList extends ArrayList<MenuGroup> {

    @Override
    public boolean add(MenuGroup menuGroup) {
        if (contains(menuGroup)) {
            throw new RuntimeException("menuGroup:" + menuGroup + " has exits.");
        }

        return super.add(menuGroup);
    }
}
