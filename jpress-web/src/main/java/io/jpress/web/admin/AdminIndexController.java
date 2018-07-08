package io.jpress.web.admin;

import com.jfinal.aop.Clear;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin")
public class AdminIndexController extends AdminControllerBase {


    @Clear
    public void login() {
        render("login.html");
    }


    @Clear
    public void doLogin() {

    }


    public void index() {

        render("index.html");
    }
}
