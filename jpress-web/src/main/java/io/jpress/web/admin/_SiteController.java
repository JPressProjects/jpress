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
import io.jpress.service.RoleService;
import io.jpress.service.SiteInfoService;
import io.jpress.web.base.AdminControllerBase;
import org.nustaq.kson.KsonStringOutput;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@RequestMapping(value = "/admin/site", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _SiteController extends AdminControllerBase {


    @Inject
    private SiteInfoService siteInfoService;

    @Inject
    private RoleService roleService;


    @AdminMenu(text = "站点",groupId = JPressConsts.SYSTEM_MENU_SYSTEM,order = 7)
    public void list() {

        Columns columns = new Columns();
        Page<SiteInfo> page = siteInfoService.paginateByColumns(getPagePara(), getPageSizePara(), columns, "created desc");
        setAttr("page",page);

        render("site/site_list.html");
    }


    public void add(){

        List<Role> roleList = roleService.findAll();
        setAttr("roleList",roleList);

        render("site/site_edit.html");
    }

    public void edit(){

        Long siteId = getLong();

        SiteInfo siteInfo = siteInfoService.findById(siteId);
        if(siteInfo!=null){
            setAttr("siteInfo",siteInfo);
        }

        List<Role> roleList = roleService.findAllWithRole(siteId);
        if(!roleList.isEmpty()){
            setAttr("roleList",roleList);
        }

        render("site/site_edit.html");
    }


    /**
     * 站点数据  保存到数据库
     */
    public void doSave(){

        SiteInfo siteInfo = getBean(SiteInfo.class, "siteInfo");

        if(siteInfo == null){
            renderFailJson("保存失败");
            return;
        }

        siteInfo.saveOrUpdate();

        //获取所有id
        Long[] roleIds = getParaValuesToLong("roleId");

        //更新中间表
        if(roleIds != null &&roleIds.length > 0){
          siteInfoService.saveOrUpdateSiteRoleMapping(siteInfo.getId(),roleIds);
        }

        renderOkJson();

    }


    public void delById(){
        render(siteInfoService.deleteById(getIdPara()) ? OK : FAIL);
    }


    public void delByIds(){

        Set<String> idsSet = getParaSet("ids");
        render(siteInfoService.batchDeleteByIds(idsSet.toArray()) ? OK : FAIL);

    }

}
