package io.jpress.web.front;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping("/pay")
public class PayController extends TemplateControllerBase {

    public void index(){
        renderText("pay index ...");
    }


    public void exec(){
        renderText("exec");
    }

    public void callback(){
        renderText("callback");
    }

}
