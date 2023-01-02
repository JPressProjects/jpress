/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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

    public MenuGroup() {
    }

    public MenuGroup(String id) {
        this.id = id;
    }

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

        if (items.contains(item)) {
            return;
        }

        items.add(item);
        items.sort(Comparator.comparingInt(MenuItem::getOrder));
    }

    public void removeItem(Predicate<? super MenuItem> filter) {
        items.removeIf(filter);
    }

    public void removeItem(MenuItem item) {
        if (items == null || items.isEmpty()) {
            return;
        }

        items.removeIf(menuitem -> item.equals(menuitem));
    }

    public boolean contains(MenuItem item) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (MenuItem i : items) {
            if (i.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public String getPermission() {
        return id;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof MenuItem == false) {
            return false;
        }
        return ((MenuItem) obj).getId().equals(id);
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
