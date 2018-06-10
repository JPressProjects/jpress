package io.jpress.web.admin;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.admin.menu.annotation.AdminMenu;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.service.Services;
import io.jpress.web.JPressAdminControllerBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/menu")
public class AdminMenuController extends JPressAdminControllerBase {

    private MenuService menuService = Services.get(MenuService.class);

    /**
     * 同步所有可以进行控制的 Action 到数据库
     */
    public void sync() {
        List<Menu> menus = buildMenu();
        int syncCount = menuService.sync(menus);

        if (syncCount == 0) {
            renderJson(Ret.ok("msg", "权限已经是最新状态，无需更新"));
        } else {
            renderJson(Ret.ok("msg", "权限更新成功，共更新权限数 : " + syncCount));
        }
    }


    // 用于排除掉 BaseController 中的几个成为了 action 的方法
    private static Set<String> excludedMethodName = buildExcludedMethodName();

    private static Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = JPressAdminControllerBase.class.getMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        return excludedMethodName;
    }

    private static List<Menu> buildMenu() {
        List<Menu> menus = new ArrayList<>();
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

                Menu menu = new Menu();
                menu.setText(adminMenu.text());
                menu.setIcon(adminMenu.icon());
                menu.setTarget(adminMenu.target());
                menu.setFlag(adminMenu.flag());
                menu.setDescNo(adminMenu.desc_no());

                menus.add(menu);
            }
        }

        return menus;
    }

}
