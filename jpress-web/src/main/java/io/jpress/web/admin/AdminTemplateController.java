package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.service.MenuService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/template")
public class AdminTemplateController extends AdminControllerBase {

    @Inject
    private MenuService menuService;

    /**
     * 同步所有可以进行控制的 Action 到数据库
     */
    @AdminMenu(text = "模板", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 2)
    public void index() {
        render("template.html");
    }


}
