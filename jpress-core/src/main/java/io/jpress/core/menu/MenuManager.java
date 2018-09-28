package io.jpress.core.menu;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.core.module.Module;
import io.jpress.core.module.ModuleManager;
import io.jpress.core.module.Modules;
import io.jpress.web.base.AdminControllerBase;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class MenuManager {

    private static final MenuManager me = new MenuManager();

    private List<MenuGroup> systemMenus = new ArrayList<>();
    private List<MenuGroup> moduleMenus = new ArrayList<>();
    private List<MenuGroup> ucenterMenus = new ArrayList<>();

    public static MenuManager me() {
        return me;
    }

    public void init() {
        initAdminSystemMenuGroup();

        initAdminMenuItems();

        initUCenterMenuItems();
    }


    /**
     * 初始化 系统固定的菜单组
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminSystemMenuGroup() {


//        MenuGroup statisticsMenuGroup = new MenuGroup();
//        statisticsMenuGroup.setId(JPressConstants.SYSTEM_MENU_STATISTICS);
//        statisticsMenuGroup.setText("运营");
//        systemMenus.add(statisticsMenuGroup);


//        MenuGroup orderMenuGroup = new MenuGroup();
//        orderMenuGroup.setId(JPressConstants.SYSTEM_MENU_FINANCE);
//        orderMenuGroup.setText("财务");
//        systemMenus.add(orderMenuGroup);


        MenuGroup userMenuGroup = new MenuGroup();
        userMenuGroup.setId(JPressConstants.SYSTEM_MENU_USER);
        userMenuGroup.setText("用户");
        userMenuGroup.setIcon("<i class=\"fa fa-fw fa-user\"></i>");
        systemMenus.add(userMenuGroup);


        MenuGroup wechatMenuGroup = new MenuGroup();
        wechatMenuGroup.setId(JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT);
        wechatMenuGroup.setText("微信");
        wechatMenuGroup.setIcon("<i class=\"fa fa-fw fa-wechat\"></i>");
        systemMenus.add(wechatMenuGroup);


        MenuGroup templateMenuGroup = new MenuGroup();
        templateMenuGroup.setId(JPressConstants.SYSTEM_MENU_TEMPLATE);
        templateMenuGroup.setText("模板");
        templateMenuGroup.setIcon("<i class=\"fa fa-magic\"></i>");
        systemMenus.add(templateMenuGroup);


        MenuGroup settingMenuGroup = new MenuGroup();
        settingMenuGroup.setId(JPressConstants.SYSTEM_MENU_SYSTEM);
        settingMenuGroup.setText("系统");
        settingMenuGroup.setIcon("<i class=\"fa fa-cog\"></i>");
        systemMenus.add(settingMenuGroup);

    }

    /**
     * 初始化 模块菜单组
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminModuleMenuGroup(Modules modules) {
        for (Module module : modules.getList()) {
            List<MenuGroup> menus = module.getAdminMenus();
            if (menus != null) {
                moduleMenus.addAll(menus);
            }
        }

        MenuGroup attachmentMenuGroup = new MenuGroup();
        attachmentMenuGroup.setId(JPressConstants.SYSTEM_MENU_ATTACHMENT);
        attachmentMenuGroup.setText("附件");
        attachmentMenuGroup.setIcon("<i class=\"fa fa-fw fa-folder-open\"></i>");
        moduleMenus.add(attachmentMenuGroup);
    }


    /**
     * 初始化 子菜单
     */
    private void initAdminMenuItems() {

        initAdminModuleMenuGroup(ModuleManager.me().getModules());
        List<MenuItem> adminMenuItems = buildAdminMenuItems();

        for (MenuItem item : adminMenuItems) {
            MenuGroup group = getAdminGroup(item.getGroupId());
            if (group != null) group.addItem(item);
        }

    }

    private void initUCenterMenuItems() {


        for (Module module : ModuleManager.me().getModules().getList()) {
            List<MenuGroup> menus = module.getUcenternMenus();
            if (menus != null) {
                ucenterMenus.addAll(menus);
            }
        }

        List<MenuItem> ucenterMenuItems = buildUCenterMenuItems();
        for (MenuItem item : ucenterMenuItems) {
            MenuGroup group = getUcenterGroup(item.getGroupId());
            if (group != null) group.addItem(item);
        }

    }

    private MenuGroup getAdminGroup(String id) {
        for (MenuGroup group : systemMenus) {
            if (id.equals(group.getId())) {
                return group;
            }
        }

        for (MenuGroup group : moduleMenus) {
            if (id.equals(group.getId())) {
                return group;
            }
        }

        return null;
    }

    private MenuGroup getUcenterGroup(String id) {
        for (MenuGroup group : ucenterMenus) {
            if (id.equals(group.getId())) {
                return group;
            }
        }


        return null;
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
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/admin")) {

                Action action = JFinal.me().getAction(actionKey, urlPara);
                if (action == null || excludedMethodName.contains(action.getMethodName())) {
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

                UCenterMenu adminMenu = action.getMethod().getAnnotation(UCenterMenu.class);
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


    public List<MenuGroup> getSystemMenus() {
        systemMenus.sort(Comparator.comparingInt(MenuGroup::getOrder));
        return systemMenus;
    }

    public List<MenuGroup> getModuleMenus() {
        moduleMenus.sort(Comparator.comparingInt(MenuGroup::getOrder));
        return moduleMenus;
    }

    public List<MenuGroup> getUcenterMenus() {
        ucenterMenus.sort(Comparator.comparingInt(MenuGroup::getOrder));
        return ucenterMenus;
    }
}
