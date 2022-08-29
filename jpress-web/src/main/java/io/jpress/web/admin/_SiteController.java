package io.jpress.web.admin;


import com.jfinal.aop.Inject;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.SiteInfo;
import io.jpress.service.RoleService;
import io.jpress.service.SiteInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Set;

@RequestMapping(value = "/admin/site", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _SiteController extends AdminControllerBase {


    @Inject
    private SiteInfoService siteInfoService;

    @Inject
    private RoleService roleService;


    @AdminMenu(text = "站点", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 7)
    public void list() {
        List<SiteInfo> siteInfos = siteInfoService.findAll();
        setAttr("siteInfos",siteInfos);
        render("site/site_list.html");
    }

    public void edit() {

        //获取site id
        Long siteId = getLong();

        //根据site id 查询 site信息
        SiteInfo siteInfo = siteInfoService.findById(siteId);
        if (siteInfo != null) {
            setAttr("siteInfo", siteInfo);
        }

        if (siteInfo != null) {
            setAttr("currentLangs", siteInfo.getBindLangsAsSet());
        }


        render("site/site_edit.html");

    }

    /**
     * 站点数据  保存到数据库
     */
    public void doSave() {

        SiteInfo siteInfo = getBean(SiteInfo.class, "siteInfo");

        if (siteInfo == null) {
            renderFailJson("保存失败");
            return;
        }

        if (siteInfo.getName() == null) {
            renderFailJson("站点名称不能为空");
            return;
        }

        if(siteInfo.getSiteId() == null || siteInfo.getSiteId() <= 0){
            renderFailJson("自定义站点ID不能为空且必须大于等于零");
            return;
        }

        //如果填写了 域名 但是 域名以 http:// 或者 https:// 开头的 则不行
        if (siteInfo.getBindDomain() != null && (siteInfo.getBindDomain().startsWith("http://") || siteInfo.getBindDomain().startsWith("https://"))) {
            renderFailJson("域名不能以http://或者https://开头");
            return;
        }

        //如果用户填写了 二级目录 但是目录不以 / 开头 则不行
        if (siteInfo.getBindPath() != null && !siteInfo.getBindPath().startsWith("/")) {
            renderFailJson("绑定目录必须以/开头");
            return;
        }


        //获取所有绑定的语言
        String[] bindLanguages = getParaValues("bindLang");
        siteInfo.setBindLangs(ArrayUtil.toString(bindLanguages, ","));


        siteInfoService.saveOrUpdate(siteInfo);

        renderOkJson();

    }


    public void delById() {
        render(siteInfoService.deleteById(getIdPara()) ? OK : FAIL);
    }


    public void delByIds() {

        Set<String> idsSet = getParaSet("ids");
        render(siteInfoService.batchDeleteByIds(idsSet.toArray()) ? OK : FAIL);

    }


    public void change() {
        SiteInfo siteInfo = siteInfoService.findById(getParaToLong("id"));
        if (siteInfo != null) {
            CookieUtil.put(this, JPressConsts.COOKIE_ADMIN_SITE_ID, siteInfo.getSiteId());
        } else {
            CookieUtil.remove(this, JPressConsts.COOKIE_ADMIN_SITE_ID);
        }


        String referer = RequestUtil.getReferer(getRequest());
        if (StrUtil.isBlank(referer)) {
            referer = "/admin/index";
        }

        redirect(referer);

    }

}
