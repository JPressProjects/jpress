/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.addon.controller;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.Routes;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import io.jboot.core.JbootCoreConfig;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootActionMapping;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.MenuItem;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.web.interceptor.JPressInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AddonControllerManager {

    private static Routes addonRoutes = new AddonRoutes();
    private static AddonActionMapping actionMapping = new AddonActionMapping(addonRoutes);

    private static Map<Class<?>, String> controllerAddonMapping = new ConcurrentHashMap<>();


    public static void addController(Class<? extends Controller> controllerClass, String addonId) {
        String[] mappingAndViewPath = JbootCoreConfig.getMappingAndViewPath(controllerClass);
        if (mappingAndViewPath == null){
            return;
        }

        String mapping = mappingAndViewPath[0];
        String viewPath = mappingAndViewPath[1];

        if (StrUtil.isBlank(mapping)){
            return;
        }

        // 尝试去清除 Controller 以保障绝对安全, 虽然插件在 stop() 的时候会去清除
        // 但是由于可能 stop() 出错等原因，没有执行到 deletController 的操作
        deleteController(controllerClass);

        if (StrUtil.isBlank(viewPath)) {
            viewPath = "/";
        } else if (viewPath.indexOf("/") != 0) {
            viewPath = "/" + viewPath;
        }

        addonRoutes.add(mapping, controllerClass, "/addons/" + addonId + viewPath);
        controllerAddonMapping.put(controllerClass, addonId);
    }


    public static List<String> getAllActionKeys() {
        return actionMapping.getAllActionKeys();
    }


    public static void deleteController(Class<? extends Controller> c) {
        RequestMapping mapping = c.getAnnotation(RequestMapping.class);
        if (mapping == null) {
            return;
        }

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) {
            return;
        }

        addonRoutes.getRouteItemList().removeIf(route -> route.getControllerPath().equals(value));
//        Routes.getControllerKeySet().removeIf(actionKey -> Objects.equals(actionKey, value));
        controllerAddonMapping.remove(c);
    }


    public static void buildActionMapping() {
        deleteAddonMenus();
        actionMapping.buildActionMapping();
        addAddonMenus();
    }


    private static void addAddonMenus() {
        MenuManager.me().addMenuItems(buildAdminMenuItems());
        MenuManager.me().addMenuItems(buildUcenterMenuItems());
    }


    private static void deleteAddonMenus() {
        List<MenuItem> adminMenuItems = buildAdminMenuItems();
        for (MenuItem menuItem : adminMenuItems) {
            MenuManager.me().deleteMenuItem(menuItem.getId());
        }
        List<MenuItem> ucenterMenuItems = buildUcenterMenuItems();
        for (MenuItem menuItem : ucenterMenuItems) {
            MenuManager.me().deleteMenuItem(menuItem.getId());
        }
    }


    private static List<MenuItem> buildUcenterMenuItems() {
        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = actionMapping.getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理 ucenter 开头的菜单
            if (actionKey.startsWith("/ucenter")) {

                Action action = getAction(actionKey, urlPara);
                if (action == null) {
                    continue;
                }

                UCenterMenu uCenterMenu = action.getMethod().getAnnotation(UCenterMenu.class);
                if (uCenterMenu == null) {
                    continue;
                }

                adminMenuItems.add(new MenuItem(uCenterMenu, actionKey));
            }
        }

        return adminMenuItems;
    }

    public static List<MenuItem> buildAdminMenuItems() {

        List<MenuItem> adminMenuItems = new ArrayList<>();
        List<String> allActionKeys = actionMapping.getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/admin")) {

                Action action = getAction(actionKey, urlPara);
                if (action == null) {
                    continue;
                }

                AdminMenu adminMenu = action.getMethod().getAnnotation(AdminMenu.class);
                if (adminMenu == null) {
                    continue;
                }

                adminMenuItems.add(new MenuItem(adminMenu, actionKey));
            }
        }

        return adminMenuItems;
    }

    public static Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }


    /**
     * 此拦截器是作用于所有的插件加载进来的 Controller
     * 设置了 APATH 这个常量，方便插件自己的 Controller 去渲染自己目录下的静态资源文件，例如：css、js等
     * 例如，在html引入自己插件下的css内容，可以这么写
     * <link rel="stylesheet" href="#(CPATH)#(APATH)/css/myaddon.css">
     */
    public static class AddonControllerInterceptor implements Interceptor {
        @Override
        public void intercept(Invocation inv) {
            String addonId = controllerAddonMapping.get(inv.getController().getClass());
            inv.getController().set(JPressInterceptor.ADDON_PATH_KEY, "/addons/" + addonId);
            inv.invoke();
        }
    }


    /**
     * 自定义自己的ActionMapping的原因主要有以下几点
     * <p>
     * 1、ActionMapping 的 mapping 是 hashMap，随时对这个 mapping 进行操作可能存在线程不安全的问题，所以需要修改为 ConcurrentHashMap
     * 2、需要把 buildActionMapping() 方法给公布出来，才能在对 mapping 进行操作的时候重新构建 actionKey->Controller 的映射关系
     */
    public static class AddonActionMapping extends JbootActionMapping {

        public AddonActionMapping(Routes routes) {
            super(routes);
            this.mapping = new ConcurrentHashMap<>();
        }

        @Override
        public void buildActionMapping() {
            super.buildActionMapping();
        }


        @Override
        public Action getAction(String url, String[] urlPara) {
            return super.getAction(url, urlPara);
        }
    }


    public static class AddonRoutes extends Routes {

        public AddonRoutes() {
            //setClearAfterMapping(false) 不让 AddonActionMapping 在构建完毕后对已经添加的 Routes 进行清除的工作
            setClearAfterMapping(false);

            //通过 AddonControllerInterceptor 拦截器设置每个插件自己的资源路径
            addInterceptor(new AddonControllerInterceptor());
        }

        @Override
        public void config() {
            //do nothing
        }
    }
}
