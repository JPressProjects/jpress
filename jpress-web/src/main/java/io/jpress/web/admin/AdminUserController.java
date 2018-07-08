package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.service.RoleService;
import io.jpress.core.web.base.AdminControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/user")
public class AdminUserController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @AdminMenu(text = "用户", groupId = JPressConstants.SYSTEM_MENU_USER, order = 0)
    public void index() {
        render("user.html");

    }

    @AdminMenu(text = "我的资料", groupId = JPressConstants.SYSTEM_MENU_USER)
    public void my() {
        render("my.html");
    }

}
