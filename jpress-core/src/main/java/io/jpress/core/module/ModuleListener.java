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

    /**
     * 对后台的面板进行渲染
     * <p>
     * 返回的内容是html 文件地址，html 文件应该只包含 单个div ，这个 div 会被嵌套在 面板里
     * <p>
     * 暂时不支持顺序
     *
     * @param controller
     * @return 返回要 html 文件地址
     */
    public String onRenderDashboardBox(Controller controller);


    /**
     * 配置后台的菜单
     *
     * @param adminMenus
     */
    public void onConfigAdminMenu(List<MenuGroup> adminMenus);


    /**
     * 配置用户中心的菜单
     *
     * @param ucenterMenus
     */
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus);

}
