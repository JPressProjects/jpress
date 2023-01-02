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

import com.jfinal.core.JFinal;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class MenuItem {

    private String id;
    private String text;
    private String icon;
    private String groupId;
    private String url;
    private int order = 100;
    private String target;

    public MenuItem() {
    }

    public MenuItem(UCenterMenu uCenterMenu,String actionKey) {
        this.setText(uCenterMenu.text());
        this.setIcon(uCenterMenu.icon());
        this.setGroupId(uCenterMenu.groupId());
        this.setUrl(actionKey);
        this.setOrder(uCenterMenu.order());
        this.setTarget(uCenterMenu.target());
    }

    public MenuItem(AdminMenu adminMenu, String actionKey) {
        this.setText(adminMenu.text());
        this.setIcon(adminMenu.icon());
        this.setGroupId(adminMenu.groupId());
        this.setUrl(actionKey);
        this.setOrder(adminMenu.order());
        this.setTarget(adminMenu.target());
    }

    public String getId() {
        return id != null ? id : text + "--" + url;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUrl() {
        return JFinal.me().getContextPath() + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPermission() {
        return groupId + ":" + url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
        return "MenuItem{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", groupId='" + groupId + '\'' +
                ", url='" + url + '\'' +
                ", order=" + order +
                '}';
    }
}
