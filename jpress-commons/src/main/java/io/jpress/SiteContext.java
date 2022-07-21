package io.jpress;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;

public class SiteContext {

    private static ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void setSiteId(Long siteId){
        TL.set(siteId);
    }

    public static Long getSiteId(){
        Long siteId = TL.get();
        return siteId == null ? 0L : siteId;
    }

    public static void removeSiteId(){
        TL.remove();
    }

    public static String getSitePath(){
        Controller controller = JbootControllerContext.get();
        if (controller == null){
            return "";
        }

        Model site = controller.getAttr("SITE");
        if (site == null){
            return "";
        }

        String path = site.getStr("bind_path");
        return StrUtil.isNotBlank(path) ? path : "";
    }

}
