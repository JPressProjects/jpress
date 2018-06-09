package io.jpress.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.admin
 */
public class LeftMenu {

    private List<LeftMenuItem> items;


    public List<LeftMenuItem> getItems() {
        return items;
    }

    public void setItems(List<LeftMenuItem> items) {
        this.items = items;
    }


    public void addItem(LeftMenuItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }

        items.add(item);
    }

    public boolean isEmpty() {
        return items != null && items.size() > 0;
    }
}
