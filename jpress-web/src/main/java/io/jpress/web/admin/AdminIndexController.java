package io.jpress.web.admin;

import com.jfinal.aop.Clear;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.admin.menu.AdminMenuGroup;
import io.jpress.admin.menu.AdminMenuManager;
import io.jpress.web.JPressAdminControllerBase;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin")
public class AdminIndexController extends JPressAdminControllerBase {


    @Clear
    public void login() {
        render("/WEB-INF/views/admin/login.html");
    }


    @Clear
    public void doLogin() {

    }


    public void index() {

        List<AdminMenuGroup> systemMenuGroups = AdminMenuManager.me().getSystemMenus();
        List<AdminMenuGroup> moduleMenuGroups = AdminMenuManager.me().getModuleMenus();

        setAttr("systemMenuGroups", systemMenuGroups);
        setAttr("moduleMenuGroups", moduleMenuGroups);

        render("/WEB-INF/views/admin/index.html");
    }
}
