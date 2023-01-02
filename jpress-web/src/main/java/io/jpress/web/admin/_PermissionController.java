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
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.annotation.AdminPermission;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuItem;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Permission;
import io.jpress.service.PermissionService;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/permission", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _PermissionController extends AdminControllerBase {

    @Inject
    private PermissionService permissionService;

    @AdminMenu(text = "权限", groupId = JPressConsts.SYSTEM_MENU_USER, order = 10)
    public void list() {
        String type = getPara("type");


        List<Permission> permissions = type == null
                ? permissionService.findAll()
                : permissionService.findListByType(type);

        setAttr("permissionGroup", PermissionKits.groupPermission(permissions));

        render("user/permission.html");
    }


    /**
     * 同步所有可以进行控制的 Action 到数据库
     */
    public void syncPermissions() {
        List<Permission> adminPermissions = buildActionPermissions();
        adminPermissions.addAll(buildMenuPermissions());

        int syncCount = permissionService.sync(adminPermissions);

        if (syncCount == 0) {
            renderJson(Ret.fail("message", "权限已经是最新状态，无需同步"));
        } else {
            renderJson(Ret.ok("message", "权限同步成功，共同步权限数 : " + syncCount));
        }
    }


    /**
     * 构建菜单相关的权限
     *
     * @return 返回所有菜单权限列表
     */
    private List<Permission> buildMenuPermissions() {

        List<MenuGroup> adminMenuGroups = new ArrayList<>();
        adminMenuGroups.addAll(MenuManager.me().getSystemMenus());
        adminMenuGroups.addAll(MenuManager.me().getModuleMenus());

        List<Permission> permissions = new ArrayList<>();
        for (MenuGroup menuGroup : adminMenuGroups) {

            Permission groupPermission = new Permission();
            groupPermission.setType(Permission.TYPE_MENU);
            groupPermission.setText(menuGroup.getText());
            groupPermission.setNode(menuGroup.getId());
            groupPermission.setActionKey(menuGroup.getId());
            permissions.add(groupPermission);

            // 如果该菜单没有 子菜单，则不用做任何处理
            if (menuGroup.getItems() == null) {
                continue;
            }

            for (MenuItem item : menuGroup.getItems()) {
                Permission itemPermission = new Permission();
                itemPermission.setType(Permission.TYPE_MENU);
                itemPermission.setText(item.getText());
                itemPermission.setNode(item.getGroupId());
                itemPermission.setActionKey(item.getPermission());
                permissions.add(itemPermission);
            }
        }

        return permissions;
    }


    // 用于排除掉 AdminControllerBase 中的几个成为了 action 的方法
    private static Set<String> excludedMethodName = buildExcludedMethodName();

    private static Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = AdminControllerBase.class.getMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        return excludedMethodName;
    }

    /**
     * 构建 action 的权限，每个Controller的方法对应一个action
     *
     * @return
     */
    private static List<Permission> buildActionPermissions() {
        List<Permission> permissions = new ArrayList<>();

        List<String> allActionKeys = AddonControllerManager.getAllActionKeys();
        allActionKeys.addAll(JFinal.me().getAllActionKeys());

        String[] urlPara = new String[1];

        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/admin") || actionKey.startsWith("/api")) {

                Action action = JFinal.me().getAction(actionKey, urlPara);

                //去获取 插件的 Action
                if (action == null){
                    action = AddonControllerManager.getAction(actionKey,urlPara);
                }

                if (action == null || excludedMethodName.contains(action.getMethodName())) {
                    continue;
                }

                AdminPermission permissionAnnotation = action.getMethod().getAnnotation(AdminPermission.class);
                String text = permissionAnnotation == null ? null : permissionAnnotation.value();
                String controller = action.getControllerClass().getName();

                Permission permission = new Permission();
                permission.setActionKey(actionKey);
                permission.setNode(controller);
                permission.setText(text);
                permission.setType(Permission.TYPE_ACTION);

                permissions.add(permission);
            }
        }

        return permissions;
    }

}
