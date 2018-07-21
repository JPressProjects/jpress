package io.jpress.core.menu;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.module.Module;
import io.jpress.module.Modules;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class AdminMenuManager {

    private static final AdminMenuManager me = new AdminMenuManager();

    private List<AdminMenuGroup> systemMenus = new ArrayList<>();
    private List<AdminMenuGroup> moduleMenus = new ArrayList<>();

    public static AdminMenuManager me() {
        return me;
    }

    public void init(Modules modules) {
        initAdminSystemMenuGroup();
        initAdminModuleMenuGroup(modules);

        initAdminMenuItems();
    }


    /**
     * 初始化 系统固定的菜单组
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminSystemMenuGroup() {


//        AdminMenuGroup statisticsMenuGroup = new AdminMenuGroup();
//        statisticsMenuGroup.setId(JPressConstants.SYSTEM_MENU_STATISTICS);
//        statisticsMenuGroup.setText("运营");
//        systemMenus.add(statisticsMenuGroup);


        AdminMenuGroup orderMenuGroup = new AdminMenuGroup();
        orderMenuGroup.setId(JPressConstants.SYSTEM_MENU_FINANCE);
        orderMenuGroup.setText("财务");
        systemMenus.add(orderMenuGroup);


        AdminMenuGroup userMenuGroup = new AdminMenuGroup();
        userMenuGroup.setId(JPressConstants.SYSTEM_MENU_USER);
        userMenuGroup.setText("用户");
        systemMenus.add(userMenuGroup);


        AdminMenuGroup wechatMenuGroup = new AdminMenuGroup();
        wechatMenuGroup.setId(JPressConstants.SYSTEM_MENU_WECHAT);
        wechatMenuGroup.setText("微信");
        systemMenus.add(wechatMenuGroup);


        AdminMenuGroup settingMenuGroup = new AdminMenuGroup();
        settingMenuGroup.setId(JPressConstants.SYSTEM_MENU_SYSTEM);
        settingMenuGroup.setText("系统");
        systemMenus.add(settingMenuGroup);


//        AdminMenuGroup toolMenuGroup = new AdminMenuGroup();
//        toolMenuGroup.setId(JPressConstants.SYSTEM_MENU_TOOL);
//        toolMenuGroup.setText("工具");
//        systemMenus.add(toolMenuGroup);


        AdminMenuGroup attachmentMenuGroup = new AdminMenuGroup();
        attachmentMenuGroup.setId(JPressConstants.SYSTEM_MENU_ATTACHMENT);
        attachmentMenuGroup.setText("附件");
        systemMenus.add(attachmentMenuGroup);

    }

    /**
     * 初始化 模块菜单组
     * 备注：子初始化菜单组，不初始化子菜单，子菜单由注解完成
     */
    private void initAdminModuleMenuGroup(Modules modules) {
        for (Module module : modules.getModules()) {
            moduleMenus.addAll(module.getMenuGroups());
        }
    }

    /**
     * 初始化 子菜单
     */
    private void initAdminMenuItems() {
        List<AdminMenuItem> adminMenuItems = buildAdminMenuItems();

        for (AdminMenuItem item : adminMenuItems) {
            AdminMenuGroup group = getGroup(item.getGroupId());
            if (group != null) group.addItem(item);
        }

    }

    private AdminMenuGroup getGroup(String id) {
        for (AdminMenuGroup group : systemMenus) {
            if (id.equals(group.getId())) {
                return group;
            }
        }

        for (AdminMenuGroup group : moduleMenus) {
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

    private static List<AdminMenuItem> buildAdminMenuItems() {
        List<AdminMenuItem> adminMenuItems = new ArrayList<>();
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

                AdminMenuItem menu = new AdminMenuItem();
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


    public List<AdminMenuGroup> getSystemMenus() {
        systemMenus.sort(Comparator.comparingInt(AdminMenuGroup::getOrder));
        return systemMenus;
    }

    public List<AdminMenuGroup> getModuleMenus() {
        moduleMenus.sort(Comparator.comparingInt(AdminMenuGroup::getOrder));
        return moduleMenus;
    }
}
