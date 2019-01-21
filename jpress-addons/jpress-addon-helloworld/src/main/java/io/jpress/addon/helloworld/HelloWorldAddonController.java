package io.jpress.addon.helloworld;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.addon.controller.AddonController;


@RequestMapping("/helloworld")
public class HelloWorldAddonController extends AddonController {

    public void index(){
        render("index.html");
    }
}
