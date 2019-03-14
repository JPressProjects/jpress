/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.MenuItem;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.menu.annotation.UCenterMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AddonControllerManager {

    private static Routes addonRoutes = new Routes() {
        @Override
        public void config() {}
    };


    private static Map<Class, String> controllerAddonMapping = new ConcurrentHashMap<>();

    private static AddonActionMapping actionMapping = new AddonActionMapping(addonRoutes);

    public static void addController(Class<? extends Controller> c, String addonId) {
        RequestMapping mapping = c.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        // 尝试去清除 Controller 以保障绝对安全, 虽然插件在 stop() 的时候会去清除
        // 但是由于可能 stop() 出错等原因，没有执行到 deletController 的操作
        deleteController(c);

        String viewPath = AnnotationUtil.get(mapping.viewPath());
        if (StrUtil.isBlank(viewPath) || "/".equals(viewPath)) {
            addonRoutes.add(value, c, "addons/" + addonId);
        } else {
            addonRoutes.add(value, c, viewPath);
        }
        controllerAddonMapping.put(c, addonId);
    }

    public static void deleteController(Class<? extends Controller> c) {
        RequestMapping mapping = c.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        addonRoutes.getRouteItemList().removeIf(route -> route.getControllerKey().equals(value));
        Routes.getControllerKeySet().removeIf(actionKey -> Objects.equals(actionKey, value));
        controllerAddonMapping.remove(c);
    }


    public static void buildActionMapping() {
        deleteAddonMenus();
        actionMapping.buildActionMapping();
        addAddonMenus();
    }


    private static void deleteAddonMenus() {
        List<MenuItem> menuItems = buildAdminMenuItems();
        for (MenuItem menuItem : menuItems) MenuManager.me().deleteMenuItem(menuItem.getId());
    }

    private static void addAddonMenus() {
        MenuManager.me().addMenuItems(buildAdminMenuItems());
        MenuManager.me().addMenuItems(buildUcenterMenuItems());
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
            inv.getController().set("APATH", "/addons/" + addonId + "/");
            inv.invoke();
        }
    }


    /**
     * 自定义自己的ActionMapping的原因主要有以下几点
     *
     * 1、ActionMapping 的 mapping 是 hashMap，随时对这个 mapping 进行操作可能存在线程不安全的问题
     * 2、需要把 buildActionMapping() 方法给公布出来，才能在对 mapping 进行操作的时候重新构建 actionKey->Controller 的映射关系
     * 3、需要给 所有的插件的 Controller 添加一个全局的拦截器 AddonControllerInterceptor，通过拦截器设置每个插件自己的资源路径
     * 4、需要配置 Routes.setClearAfterMapping(false) 不让 AddonActionMapping 在构建完毕后对 Routes 进行清除的工作
     */
    public static class AddonActionMapping extends ActionMapping {

        public AddonActionMapping(Routes routes) {
            super(routes);
            routes.setClearAfterMapping(false);
            routes.addInterceptor(new AddonControllerInterceptor());
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
}
