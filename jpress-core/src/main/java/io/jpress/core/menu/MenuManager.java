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

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.JPressConsts;
import io.jpress.JPressMenuConfig;
import io.jpress.core.install.Installer;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.web.base.AdminControllerBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private MenuGroup ucenterCommentMenus = new MenuGroup(JPressConsts.UCENTER_MENU_COMMENT);
    private MenuGroup ucenterPersonalMenus = new MenuGroup(JPressConsts.UCENTER_MENU_PERSONAL_INFO);

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

    }


    /**
     * 初始化 后台的固定菜单
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminSystemMenuGroup() {

        if (JPressMenuConfig.me.isUserEnable()) {
            MenuGroup userMenuGroup = new MenuGroup();
            userMenuGroup.setId(JPressConsts.SYSTEM_MENU_USER);
            userMenuGroup.setText("用户");
            userMenuGroup.setIcon("<i class=\"fas fa-user\"></i>");
            userMenuGroup.setOrder(200);
            systemMenus.add(userMenuGroup);
        }

        if (JPressMenuConfig.me.isWechatEnable()) {
            MenuGroup wechatMenuGroup = new MenuGroup();
            wechatMenuGroup.setId(JPressConsts.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT);
            wechatMenuGroup.setText("微信");
            wechatMenuGroup.setIcon("<i class=\"fab fa-weixin\"></i>");
            wechatMenuGroup.setOrder(300);
            systemMenus.add(wechatMenuGroup);
        }


        if (JPressMenuConfig.me.isTemplateEnable()) {
            MenuGroup templateMenuGroup = new MenuGroup();
            templateMenuGroup.setId(JPressConsts.SYSTEM_MENU_TEMPLATE);
            templateMenuGroup.setText("模板");
            templateMenuGroup.setIcon("<i class=\"fas fa-magic\"></i>");
            templateMenuGroup.setOrder(400);
            systemMenus.add(templateMenuGroup);
        }


        if (JPressMenuConfig.me.isAddonEnable()) {
            MenuGroup addonMenuGroup = new MenuGroup();
            addonMenuGroup.setId(JPressConsts.SYSTEM_MENU_ADDON);
            addonMenuGroup.setText("插件");
            addonMenuGroup.setIcon("<i class=\"fas fa-plug\"></i>");
            addonMenuGroup.setOrder(500);
            systemMenus.add(addonMenuGroup);
        }


        MenuGroup settingMenuGroup = new MenuGroup();
        settingMenuGroup.setId(JPressConsts.SYSTEM_MENU_SYSTEM);
        settingMenuGroup.setText("设置");
        settingMenuGroup.setIcon("<i class=\"fas fa-cog\"></i>");
        settingMenuGroup.setOrder(600);
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
        attachmentMenuGroup.setIcon("<i class=\"fas fa-folder\"></i>");
        moduleMenus.add(attachmentMenuGroup);


        addMenuItems(buildAdminMenuItems());
        addMenuItems(buildUCenterMenuItems());



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

        ucenterCommentMenus.removeItem(item -> id.equals(item.getId()));
        ucenterPersonalMenus.removeItem(item -> id.equals(item.getId()));
    }


    public void deleteMenuGroup(String id) {
        systemMenus.removeIf(group -> id.equals(group.getId()));
        moduleMenus.removeIf(group -> id.equals(group.getId()));
    }


    public void addMenuItems(List<MenuItem> items) {
        if (items == null) {
            return;
        }
        items.forEach(this::addMenuItem);
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


        if (ucenterCommentMenus.getId().equals(item.getGroupId())) {
            ucenterCommentMenus.addItem(item);
        }

        if (ucenterPersonalMenus.getId().equals(item.getGroupId())) {
            ucenterPersonalMenus.addItem(item);
        }

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

            if (actionKey.startsWith("/admin") && !JPressMenuConfig.me.isExcludeActionKey(actionKey)) {
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

        List<MenuItem> menuItems = new ArrayList<>();
        List<String> allActionKeys = JFinal.me().getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/ucenter") && !JPressMenuConfig.me.isExcludeActionKey(actionKey)) {

                Action action = JFinal.me().getAction(actionKey, urlPara);
                if (action == null || excludedMethodName.contains(action.getMethodName())) {
                    continue;
                }

                UCenterMenu uCenterMenu = action.getMethod().getAnnotation(UCenterMenu.class);
                if (uCenterMenu == null) {
                    continue;
                }

                menuItems.add(new MenuItem(uCenterMenu, actionKey));
            }
        }

        return menuItems;
    }


    public List<MenuGroup> getSystemMenus() {
        return systemMenus;
    }

    public List<MenuGroup> getModuleMenus() {
        return moduleMenus;
    }

    public MenuGroup getUcenterCommentMenus() {
        return ucenterCommentMenus;
    }

    public MenuGroup getUcenterPersonalMenus() {
        return ucenterPersonalMenus;
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }


}
