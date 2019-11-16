package io.jpress.addon.helloworld;

import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;


@RequestMapping(value = "/helloworld",viewPath = "/")
public class HelloWorldAddonController extends JbootController {

    public void index() {
        setAttr("version","1.0.2");
        render("helloworld/index.html");
    }

    public void json() {
        renderJson(Ret.ok().set("message", "json ok...."));
    }

    @ActionKey("/admin/addon/test")
    @AdminMenu(groupId = JPressConsts.SYSTEM_MENU_ADDON, text = "插件测试")
    public void adminmenutest() {
        renderText("addon test abc");
    }
}
