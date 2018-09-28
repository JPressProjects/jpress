package io.jpress.module.page;

import com.jfinal.core.Controller;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 应用启动监听器
 * @Package io.jpress.module.page
 */
public class PageModuleLisenter implements ModuleListener {


    @Override
    public String onRenderDashboardBox(Controller controller) {
        return null;
    }


    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("page");
        menuGroup.setText("页面");
        menuGroup.setIcon("<i class=\"fa fa-fw fa-file\"></i>");
        menuGroup.setOrder(2);

        adminMenus.add(menuGroup);

    }

    @Override
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus) {
        //do nothing
    }
}
