package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/wechat")
public class AdminWechatController extends AdminControllerBase {




    @AdminMenu(text = "默认回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 0)
    public void replay() {
        render("user.html");
    }


    @AdminMenu(text = "聊天回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 1)
    public void keyword() {
        render("user.html");
    }


    @AdminMenu(text = "素材管理", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 3)
    public void material() {
        render("user.html");
    }


    @AdminMenu(text = "基础设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 11)
    public void base() {
        render("my.html");
    }


    @AdminMenu(text = "菜单设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 12)
    public void menu() {
        render("user.html");
    }



    @AdminMenu(text = "小程序", groupId = JPressConstants.SYSTEM_MENU_WECHAT, order = 20)
    public void app() {
        render("my.html");
    }




}
