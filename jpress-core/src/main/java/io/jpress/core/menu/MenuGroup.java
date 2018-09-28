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
public class MenuGroup {

    private int order = 100;
    private String text;
    private String icon;
    private String id;

    private List<MenuItem> items;


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

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public void addItem(MenuItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        items.sort(Comparator.comparingInt(MenuItem::getOrder));
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public String getPermission() {
        return id;
    }

    @Override
    public String toString() {
        return "MenuGroup{" +
                "order=" + order +
                ", text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}
