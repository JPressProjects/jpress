package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.model.InterfaceApp;
import io.jpress.service.InterfaceAppService;
import io.jpress.service.OptionService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/setting")
public class _SettingController extends AdminControllerBase {

    @Inject
    private InterfaceAppService ias;

    @Inject
    private OptionService os;

    @AdminMenu(text = "常规", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 0)
    public void index() {
        render("setting/base.html");

    }

    @AdminMenu(text = "通信", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 9)
    public void connection() {
        render("setting/connection.html");
    }


    @AdminMenu(text = "接口", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 10)
    public void app() {
        List<InterfaceApp> apps = ias.findAll();
        setAttr("apps", apps);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("app", ias.findById(id));
        }

        render("setting/app.html");

    }

    public void doAppDel() {

    }

    public void doAppSave() {
        InterfaceApp apiApplication = getBean(InterfaceApp.class, "app");
        ias.saveOrUpdate(apiApplication);
        redirect("/admin/setting/app");
    }

    @AdminMenu(text = "登录注册", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 32)
    public void reg() {
        render("setting/reg.html");
    }


    @AdminMenu(text = "网站加速", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 33)
    public void cdn() {
        render("setting/cdn.html");
    }


    @AdminMenu(text = "搜索优化", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 111)
    public void seo() {
        render("setting/seo.html");
    }



}
