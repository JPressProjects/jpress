/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.JPressConsts;
import io.jpress.core.install.Installer;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.web.base.AdminControllerBase;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class MenuManager implements JbootEventListener {

    private static final MenuManager me = new MenuManager();

    private MenuArrayList systemMenus = new MenuArrayList();
    private MenuArrayList moduleMenus = new MenuArrayList();
    private MenuArrayList ucenterMenus = new MenuArrayList();

    private MenuManager() {

    }

    public static MenuManager me() {
        return me;
    }

    public void init() {


        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        //初始化后台的固定菜单
        initAdminSystemMenuGroup();

        //初始化后台的 module 菜单
        initAdminMenuItems();

        //初始化 用户中心菜单
        initUCenterMenuItems();
    }


    /**
     * 初始化 后台的固定菜单
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminSystemMenuGroup() {

        MenuGroup orderMenuGroup = new MenuGroup();
        orderMenuGroup.setId(JPressConsts.SYSTEM_MENU_ORDER);
        orderMenuGroup.setText("财务");
        orderMenuGroup.setIcon("<i class=\"fa fa-fw fa-gg-circle\"></i>");
        systemMenus.add(orderMenuGroup);


        MenuGroup userMenuGroup = new MenuGroup();
        userMenuGroup.setId(JPressConsts.SYSTEM_MENU_USER);
        userMenuGroup.setText("用户");
        userMenuGroup.setIcon("<i class=\"fa fa-fw fa-user\"></i>");
        systemMenus.add(userMenuGroup);


        MenuGroup wechatMenuGroup = new MenuGroup();
        wechatMenuGroup.setId(JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT);
        wechatMenuGroup.setText("微信");
        wechatMenuGroup.setIcon("<i class=\"fa fa-fw fa-wechat\"></i>");
        systemMenus.add(wechatMenuGroup);


        MenuGroup templateMenuGroup = new MenuGroup();
        templateMenuGroup.setId(JPressConsts.SYSTEM_MENU_TEMPLATE);
        templateMenuGroup.setText("模板");
        templateMenuGroup.setIcon("<i class=\"fa fa-magic\"></i>");
        systemMenus.add(templateMenuGroup);


        MenuGroup addonMenuGroup = new MenuGroup();
        addonMenuGroup.setId(JPressConsts.SYSTEM_MENU_ADDON);
        addonMenuGroup.setText("插件");
        addonMenuGroup.setIcon("<i class=\"fa fa-plug\"></i>");
        systemMenus.add(addonMenuGroup);


        MenuGroup settingMenuGroup = new MenuGroup();
        settingMenuGroup.setId(JPressConsts.SYSTEM_MENU_SYSTEM);
        settingMenuGroup.setText("系统");
        settingMenuGroup.setIcon("<i class=\"fa fa-cog\"></i>");
        systemMenus.add(settingMenuGroup);

    }


    /**
     * 初始化 子菜单
     */
    private void initAdminMenuItems() {

        for (ModuleListener listener : ModuleManager.me().getListeners()) {
            listener.onConfigAdminMenu(moduleMenus);
        }

        MenuGroup attachmentMenuGroup = new MenuGroup();
        attachmentMenuGroup.setId(JPressConsts.SYSTEM_MENU_ATTACHMENT);
        attachmentMenuGroup.setText("附件");
        attachmentMenuGroup.setIcon("<i class=\"fa fa-fw fa-folder-open\"></i>");
        moduleMenus.add(attachmentMenuGroup);


        addMenuItems(buildAdminMenuItems());
    }

    public void deleteMenuItem(String id) {
        for (MenuGroup group : systemMenus) {
            if (group.getItems() != null) {
                group.getItems().removeIf(item -> item.getId().equals(id));
            }
        }
        for (MenuGroup group : moduleMenus) {
            if (group.getItems() != null) {
                group.getItems().removeIf(item -> item.getId().equals(id));
            }
        }
        for (MenuGroup group : ucenterMenus) {
            if (group.getItems() != null) {
                group.getItems().removeIf(item -> item.getId().equals(id));
            }
        }
    }

    public void deleteMenuGroup(String id) {
        systemMenus.removeIf(group -> id.equals(group.getId()));
        moduleMenus.removeIf(group -> id.equals(group.getId()));
        ucenterMenus.removeIf(group -> id.equals(group.getId()));
    }

    public void addMenuItems(List<MenuItem> items) {
        if (items == null) {
            return;
        }
        for (MenuItem item : items) {
            addMenuItem(item);
        }
    }

    public void addMenuItem(MenuItem item) {
        String ctxPath = JFinal.me().getContextPath();
        for (MenuGroup group : systemMenus) {
            if (group.getId().equals(item.getGroupId()) && item.getUrl().startsWith(ctxPath + "/admin")) {
                group.addItem(item);
            }
        }
        for (MenuGroup group : moduleMenus) {
            if (group.getId().equals(item.getGroupId()) && item.getUrl().startsWith(ctxPath + "/admin")) {
                group.addItem(item);
            }
        }
        for (MenuGroup group : ucenterMenus) {
            if (group.getId().equals(item.getGroupId()) && item.getUrl().startsWith(ctxPath + "/ucenter")) {
                group.addItem(item);
            }
        }
    }

    private void initUCenterMenuItems() {

        for (ModuleListener listener : ModuleManager.me().getListeners()) {
            listener.onConfigUcenterMenu(ucenterMenus);
        }


        MenuGroup commentMenuGroup = new MenuGroup();
        commentMenuGroup.setId("comment");
        commentMenuGroup.setText("我的评论");
        commentMenuGroup.setIcon("<i class=\"fa fa-fw fa-commenting\"></i>");
        commentMenuGroup.setOrder(88);
        ucenterMenus.add(commentMenuGroup);


        MenuGroup favoriteMenuGroup = new MenuGroup();
        favoriteMenuGroup.setId("favorite");
        favoriteMenuGroup.setText("我的收藏");
        favoriteMenuGroup.setIcon("<i class=\"fa fa-fw fa-bookmark\"></i>");
        favoriteMenuGroup.setOrder(99);
        ucenterMenus.add(favoriteMenuGroup);

        addMenuItems(buildUCenterMenuItems());
    }


    // 用于排除掉 BaseController 中的几个成为了 action 的方法
    private static Set<String> excludedMethodName = buildExcludedMethodName();

    private static Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = AdminControllerBase.class.getMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        return excludedMethodName;
    }

    private static List<MenuItem> buildAdminMenuItems() {

        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = JFinal.me().getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {

            if (actionKey.startsWith("/admin")) {
                Action action = JFinal.me().getAction(actionKey, urlPara);
                if (action == null || excludedMethodName.contains(action.getMethodName())) {
                    continue;
                }

                AdminMenu adminMenu = action.getMethod().getAnnotation(AdminMenu.class);
                if (adminMenu == null) {
                    continue;
                }

                adminMenuItems.add(new MenuItem(adminMenu, actionKey));
            }
        }

        return adminMenuItems;
    }

    private static List<MenuItem> buildUCenterMenuItems() {

        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = JFinal.me().getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/ucenter")) {

                Action action = JFinal.me().getAction(actionKey, urlPara);
                if (action == null || excludedMethodName.contains(action.getMethodName())) {
                    continue;
                }

                UCenterMenu uCenterMenu = action.getMethod().getAnnotation(UCenterMenu.class);
                if (uCenterMenu == null) {
                    continue;
                }

                adminMenuItems.add(new MenuItem(uCenterMenu, actionKey));
            }
        }

        return adminMenuItems;
    }


    public List<MenuGroup> getSystemMenus() {
        return systemMenus;
    }

    public List<MenuGroup> getModuleMenus() {
        return moduleMenus;
    }

    public List<MenuGroup> getUcenterMenus() {
        return ucenterMenus;
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }


}
