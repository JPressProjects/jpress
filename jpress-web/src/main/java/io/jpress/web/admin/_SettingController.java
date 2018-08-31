package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.model.ApiApplication;
import io.jpress.service.ApiApplicationService;

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
    private ApiApplicationService aas;

    @AdminMenu(text = "常规", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 0)
    public void index() {
        render("setting/base.html");

    }

    @AdminMenu(text = "通信", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 9)
    public void connection() {
        render("setting/connection.html");
    }


    @AdminMenu(text = "接口", groupId = JPressConstants.SYSTEM_MENU_SYSTEM, order = 10)
    public void api() {
        List<ApiApplication> apiApplications = aas.findAll();
        setAttr("apiApplications", apiApplications);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("api",aas.findById(id));
        }

        render("setting/api.html");

    }

    public void doApiDel() {

    }

    public void doApiSave() {
        ApiApplication apiApplication = getBean(ApiApplication.class, "api");
        aas.saveOrUpdate(apiApplication);
        redirect("/admin/setting/api");
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
