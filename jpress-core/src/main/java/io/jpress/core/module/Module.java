package io.jpress.core.module;

import com.jfinal.core.Controller;
import io.jpress.core.menu.AdminMenuGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 的 module
 * @Package io.jpress.module
 */
public class Module {

    private String id;
    private String name;

    private List<AdminMenuGroup> menus;

    /**
     * 用于给子类复写，返回的String是html内容，用于渲染用户登录的后台首页
     *
     * @return
     */
    public String onGetDashboardHtmlBox(Controller controller) {
        return null;
    }


    public void addMenu(String id, String text, String icon, int order) {
        if (menus == null) {
            menus = new ArrayList<>();
        }

        AdminMenuGroup menuGroup = new AdminMenuGroup();
        menuGroup.setText(text);
        menuGroup.setIcon(icon);
        menuGroup.setId(id);
        menuGroup.setOrder(order);

        menus.add(menuGroup);
    }

    public List<AdminMenuGroup> getMenus() {
        return menus;
    }

}
