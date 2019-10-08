package io.jpress.web.front;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping(value = "/pay",viewPath = "/WEB-INF/views/front/pay")
public class PayController extends TemplateControllerBase {

    public void index(){
        render("pay_wechat.html");
    }

    public void query(){

    }

    public void exec(){
        renderText("exec");
    }

    public void callback(){
        renderText("callback");
    }

}
