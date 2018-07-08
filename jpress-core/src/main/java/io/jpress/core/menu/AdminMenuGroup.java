package io.jpress.core.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class AdminMenuGroup {

    private int order = 100;
    private String text;
    private String icon;
    private String id;

    private List<AdminMenuItem> items;


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AdminMenuItem> getItems() {
        return items;
    }

    public void setItems(List<AdminMenuItem> items) {
        this.items = items;
    }

    public void addItem(AdminMenuItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        items.sort(Comparator.comparingInt(AdminMenuItem::getOrder));
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Override
    public String toString() {
        return "AdminMenuGroup{" +
                "order=" + order +
                ", text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}
