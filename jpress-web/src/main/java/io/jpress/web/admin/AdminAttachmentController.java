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
@RequestMapping("/admin/attachment")
public class AdminAttachmentController extends AdminControllerBase {


    @AdminMenu(text = "所有附件", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void officialaccount() {
        render("user.html");

    }

    @AdminMenu(text = "上传", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void wxapp() {
        render("my.html");
    }


    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_ATTACHMENT, order = 2)
    public void wxpay() {
        render("my.html");
    }

}
