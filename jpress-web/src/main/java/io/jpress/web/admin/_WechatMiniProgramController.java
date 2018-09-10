//package io.jpress.web.admin;
//
//import io.jboot.web.controller.annotation.RequestMapping;
//import io.jpress.JPressConstants;
//import io.jpress.core.menu.annotation.AdminMenu;
//import io.jpress.service.WechatReplayService;
//import io.jpress.web.base.AdminControllerBase;
//
//import javax.inject.Inject;
//
///**
// * @author Michael Yang 杨福海 （fuhai999@gmail.com）
// * @version V1.0
// * @Title: 首页
// * @Package io.jpress.web.admin
// */
//@RequestMapping("/admin/miniprogram")
//public class _WechatMiniProgramController extends AdminControllerBase {
//
//    @Inject
//    private WechatReplayService wrs;
//
//
//    @AdminMenu(text = "模板", groupId = JPressConstants.SYSTEM_MENU_WECHAT_MINI_PROGRAM, order = 1)
//    public void template() {
//        render("wechat/menu.html");
//    }
//
//
//    @AdminMenu(text = "菜单", groupId = JPressConstants.SYSTEM_MENU_WECHAT_MINI_PROGRAM, order = 20)
//    public void menu() {
//        render("wechat/menu.html");
//    }
//
//
//    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_MINI_PROGRAM, order = 21)
//    public void setting() {
//        render("wechat/menu.html");
//    }
//
//}
