/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.core.addon.controller;


import com.jfinal.config.Routes;
import com.jfinal.core.Action;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.MenuItem;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AddonControllerManager {

    private static Routes routes = new Routes() {
        @Override
        public void config() {
            this.setClearAfterMapping(false);
        }

    };
    private static AddonActionMapping actionMapping = new AddonActionMapping(routes);

    public static void addController(Class<? extends Controller> controllerClass, String addonId) {
        RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        String viewPath = AnnotationUtil.get(mapping.viewPath());
        if (StrUtil.isBlank(viewPath)) {
            routes.add(value, controllerClass, "addons/" + addonId);
        } else {
            routes.add(value, controllerClass, viewPath);
        }
    }

    public static void deleteController(Class<? extends Controller> c) {
        RequestMapping mapping = c.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        routes.getRouteItemList().removeIf(route -> route.getControllerKey().equals(value));
        try {
            Field field = Routes.class.getDeclaredField("controllerKeySet");
            field.setAccessible(true);
            Set<String> routes = (Set<String>) field.get(null);
            routes.remove(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void buildActionMapping() {
        deleteAddonMenus();
        actionMapping.buildActionMapping();
        addAddonMenus();
    }


    private static void deleteAddonMenus() {
        List<MenuItem> menuItems = buildAdminMenuItems();
        for (MenuItem menuItem : menuItems) MenuManager.me().deleteMenuItem(menuItem.getId());
    }

    private static void addAddonMenus() {
        MenuManager.me().addMenuItems(buildAdminMenuItems());
        MenuManager.me().addMenuItems(buildUcenterMenuItems());
    }

    private static List<MenuItem> buildUcenterMenuItems() {
        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = actionMapping.getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理 ucenter 开头的菜单
            if (actionKey.startsWith("/ucenter")) {

                Action action = getAction(actionKey, urlPara);
                if (action == null) {
                    continue;
                }

                UCenterMenu uCenterMenu = action.getMethod().getAnnotation(UCenterMenu.class);
                if (uCenterMenu == null) {
                    continue;
                }

                MenuItem menu = new MenuItem();
                menu.setText(uCenterMenu.text());
                menu.setIcon(uCenterMenu.icon());
                menu.setGroupId(uCenterMenu.groupId());
                menu.setUrl(actionKey);
                menu.setOrder(uCenterMenu.order());

                adminMenuItems.add(menu);
            }
        }

        return adminMenuItems;
    }

    public static List<MenuItem> buildAdminMenuItems() {

        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = actionMapping.getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/admin")) {

                Action action = getAction(actionKey, urlPara);
                if (action == null) {
                    continue;
                }

                AdminMenu adminMenu = action.getMethod().getAnnotation(AdminMenu.class);
                if (adminMenu == null) {
                    continue;
                }

                MenuItem menu = new MenuItem();
                menu.setText(adminMenu.text());
                menu.setIcon(adminMenu.icon());
                menu.setGroupId(adminMenu.groupId());
                menu.setUrl(actionKey);
                menu.setOrder(adminMenu.order());

                adminMenuItems.add(menu);
            }
        }

        return adminMenuItems;
    }

    public static Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }


    public static class AddonActionMapping extends ActionMapping {

        public AddonActionMapping(Routes routes) {
            super(routes);
            routes.config();
            this.mapping = new ConcurrentHashMap<>();
        }

        @Override
        public void buildActionMapping() {
            super.buildActionMapping();
        }

//        public Action deleteAction(String target) {
//
////            return this.mapping.remove(target);
//        }

        @Override
        public Action getAction(String url, String[] urlPara) {
            return super.getAction(url, urlPara);
        }
    }
}
