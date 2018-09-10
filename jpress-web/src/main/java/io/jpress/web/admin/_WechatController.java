package io.jpress.web.admin;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.model.WechatReplay;
import io.jpress.service.WechatReplayService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/wechat")
public class _WechatController extends AdminControllerBase {

    @Inject
    private WechatReplayService wrs;


    @AdminMenu(text = "基础设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 1)
    public void base() {
        render("wechat/setting_base.html");
    }


    @AdminMenu(text = "菜单设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 2)
    public void menu() {
        render("wechat/menu.html");
    }

    @AdminMenu(text = "默认回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 10)
    public void replay() {
        render("wechat/replay_base.html");
    }


    @AdminMenu(text = "聊天回复", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 11)
    public void keyword() {
        Page<WechatReplay> page = wrs.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("wechat/replay_list.html");
    }

    public void keywordWrite() {
        int id = getParaToInt(0, 0);

        if (id > 0) {
            WechatReplay wechatReplay = wrs.findById(id);
            setAttr("wechatReplay", wechatReplay);
        }
        render("wechat/replay_write.html");
    }


//    @AdminMenu(text = "其他设置", groupId = JPressConstants.SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT, order = 13)
//    public void other() {
//        render("wechat/setting_other.html");
//    }


}
