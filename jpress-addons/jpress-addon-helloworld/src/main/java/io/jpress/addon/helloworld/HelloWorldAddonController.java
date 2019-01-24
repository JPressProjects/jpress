package io.jpress.addon.helloworld;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/helloworld")
public class HelloWorldAddonController extends JbootController {

    public void index() {
        render("helloworld/index.html");
    }

    public void json() {
        renderJson(Ret.ok().set("message", "json ok...."));
    }
}
