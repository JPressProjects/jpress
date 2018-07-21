package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.service.RoleService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/finance")
public class AdminFinanceController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @AdminMenu(text = "订单管理", groupId = JPressConstants.SYSTEM_MENU_FINANCE, order = 0)
    public void index() {
        render("order/list.html");

    }

    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_FINANCE)
    public void my() {
        render("my.html");
    }

}
