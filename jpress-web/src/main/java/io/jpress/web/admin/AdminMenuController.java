package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.admin.menu.AdminMenuManager;
import io.jpress.admin.menu.annotation.AdminMenu;
import io.jpress.service.MenuService;
import io.jpress.service.Services;
import io.jpress.web.JPressAdminControllerBase;

import java.util.Arrays;

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
    @AdminMenu(text = "菜单管理", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE)
    public void index() {

        renderText(Arrays.toString(AdminMenuManager.me().getSystemMenus().toArray()));
    }


}
