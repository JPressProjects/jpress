//package io.jpress.core.module;
//
//import com.jfinal.core.Controller;
//import io.jpress.core.menu.MenuGroup;
//
//import java.util.List;
//
///**
// * @author Michael Yang 杨福海 （fuhai999@gmail.com）
// * @version V1.0
// * @Title: JPress 的 module
// * @Package io.jpress.module
// */
//public abstract class Module {
//
//    private String id;
//    private String name;
//
////    private List<MenuGroup> adminMenus = new ArrayList<>();
////    private List<MenuGroup> ucenterMenus = new ArrayList<>();
//
//    public Module(String id, String name) {
//        this.id = id;
//        this.name = name;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * 用于给子类复写，返回的String是html内容，用于渲染用户登录的后台首页
//     *
//     * @return
//     */
//    public abstract String onGetDashboardHtmlBox(Controller controller);
//    public abstract String onConfigAdminMenu(List<MenuGroup> adminMenus);
//    public abstract String onConfigUcenterMenu(List<MenuGroup> adminMenus);
//
//
////    public void addAdminMenu(String id, String text, String icon, int order) {
////        if (adminMenus == null) {
////            adminMenus = new ArrayList<>();
////        }
////
////        MenuGroup menuGroup = new MenuGroup();
////        menuGroup.setText(text);
////        menuGroup.setIcon(icon);
////        menuGroup.setId(id);
////        menuGroup.setOrder(order);
////
////        adminMenus.add(menuGroup);
////    }
//
////    public List<MenuGroup> getAdminMenus() {
////        return adminMenus;
////    }
//
//
////    public void addUcenterMenu(String id, String text, String icon, int order) {
////        if (ucenterMenus == null) {
////            ucenterMenus = new ArrayList<>();
////        }
////
////        MenuGroup menuGroup = new MenuGroup();
////        menuGroup.setText(text);
////        menuGroup.setIcon(icon);
////        menuGroup.setId(id);
////        menuGroup.setOrder(order);
////
////        ucenterMenus.add(menuGroup);
////    }
//
////    public List<MenuGroup> getUcenternMenus() {
////        return ucenterMenus;
////    }
//
//}
