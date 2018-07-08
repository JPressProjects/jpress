package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.AdminMenuManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.service.RoleService;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/setting")
public class AdminSettingController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @AdminMenu(text = "常规", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 0)
    public void index() {
        render("setting/base.html");

    }


    @AdminMenu(text = "菜单", groupId = JPressConstants.SYSTEM_MENU_SYSTEM,order = 1)
    public void menu() {
        renderText(Arrays.toString(AdminMenuManager.me().getSystemMenus().toArray()));
    }

    public void menu111(){
        render("/WEB-INF/views/admin/menu.html");
    }

    @AdminMenu(text = "SEO", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 11)
    public void seo() {
        render("setting/seo.html");
    }


    @AdminMenu(text = "水印", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 21)
    public void watermark() {
        render("setting/watermark.html");
    }


    @AdminMenu(text = "CDN加速", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 31)
    public void cdn() {
        render("setting/cdn.html");
    }


}
