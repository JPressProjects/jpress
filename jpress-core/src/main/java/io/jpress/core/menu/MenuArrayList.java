package io.jpress.core.menu;

import java.util.ArrayList;
import java.util.Comparator;


public class MenuArrayList extends ArrayList<MenuGroup> {

    @Override
    public boolean add(MenuGroup menuGroup) {
        if (contains(menuGroup)) {
            throw new RuntimeException("menuGroup:" + menuGroup + " has exits.");
        }
        return sort(super.add(menuGroup));
    }



    private boolean  sort(boolean success){
        if (success) {
            sort(Comparator.comparingInt(MenuGroup::getOrder));
        }
        return success;
    }

}
