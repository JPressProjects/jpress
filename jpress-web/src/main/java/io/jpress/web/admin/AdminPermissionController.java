package io.jpress.web.admin;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.Permission;
import io.jpress.permission.annotation.AdminPermisssion;
import io.jpress.service.PermissionService;
import io.jpress.service.Services;
import io.jpress.web.JPressAdminControllerBase;

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
@RequestMapping("/admin/permission")
public class AdminPermissionController extends JPressAdminControllerBase {

    private PermissionService permissionService = Services.get(PermissionService.class);

    /**
     * 同步所有可以进行控制的 Action 到数据库
     */
    public void sync() {
        List<Permission> permissions = buildSystemPermission();
        int syncCount = permissionService.sync(permissions);

        if (syncCount == 0) {
            renderJson(Ret.ok("msg", "权限已经是最新状态，无需更新"));
        } else {
            renderJson(Ret.ok("msg", "权限更新成功，共更新权限数 : " + syncCount));
        }
    }


    // 用于排除掉 BaseController 中的几个成为了 action 的方法
    private static Set<String> excludedMethodName = buildExcludedMethodName();

    private static Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = JPressAdminControllerBase.class.getMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        return excludedMethodName;
    }

    private static List<Permission> buildSystemPermission() {
        List<Permission> permissions = new ArrayList<>();
        List<String> allActionKeys = JFinal.me().getAllActionKeys();

        String[] urlPara = new String[1];
        for (String actionKey : allActionKeys) {
            // 只处理后台的权限 和 API的权限
            if (actionKey.startsWith("/admin") || actionKey.startsWith("/api")) {

                Action action = JFinal.me().getAction(actionKey, urlPara);
                if (action == null || excludedMethodName.contains(action.getMethodName())) {
                    continue;
                }

                AdminPermisssion permissionAnnotation = action.getMethod().getAnnotation(AdminPermisssion.class);
                String text = permissionAnnotation == null ? null : permissionAnnotation.value();
                String controller = action.getControllerClass().getName();

                Permission permission = new Permission();
                permission.setActionKey(actionKey);
                permission.setController(controller);
                permission.setText(text);

                permissions.add(permission);
            }
        }

        return permissions;
    }

}
