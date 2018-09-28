package io.jpress.core.module;

import com.jfinal.core.Controller;
import io.jpress.core.menu.MenuGroup;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 监听器
 * @Package io.jpress
 */
public interface ModuleListener {

    public String onGetDashboardHtmlBox(Controller controller);

    public void onConfigAdminMenu(List<MenuGroup> adminMenus);

    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus);

}
