package io.jpress.module.page.controller.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.admin.menu.annotation.AdminMenu;
import io.jpress.admin.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/admin/page")
public class AdminPageController extends AdminControllerBase {

    @AdminMenu(text = "页面管理", groupId = "page")
    public void index() {
        render("page.html");
    }

    @AdminMenu(text = "新建", groupId = "page")
    public void add() {

    }
}
