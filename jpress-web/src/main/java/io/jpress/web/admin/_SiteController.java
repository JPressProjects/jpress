package io.jpress.web.admin;


import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Role;
import io.jpress.model.SiteInfo;
import io.jpress.model.User;
import io.jpress.service.SiteInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;

@RequestMapping(value = "/admin/site", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _SiteController extends AdminControllerBase {


    @Inject
    private SiteInfoService siteInfoService;


    @AdminMenu(text = "站点",groupId = JPressConsts.SYSTEM_MENU_SYSTEM,order = 7)
    public void list() {

        Columns columns = new Columns();
        Page<SiteInfo> page = siteInfoService.paginateByColumns(getPagePara(), getPageSizePara(), columns, "created desc");
        setAttr("page",page);

        render("setting/site_list.html");
    }


    public void add(){

        render("setting/site_edit.html");
    }

    public void edit(){
        render("setting/site_edit.html");
    }


    /**
     * 站点数据  保存到数据库
     */
    public void doSave(){

        SiteInfo siteInfo = getBean(SiteInfo.class, "siteInfo");

    }

    public void del(){
        siteInfoService.deleteById(getIdPara());
        renderOkJson();
    }


}
