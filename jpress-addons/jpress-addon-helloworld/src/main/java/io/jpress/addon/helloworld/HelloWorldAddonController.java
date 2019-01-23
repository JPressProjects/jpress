package io.jpress.addon.helloworld;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/helloworld")
public class HelloWorldAddonController extends JbootController {

    public void index(){
        render("index.html");
    }
}
