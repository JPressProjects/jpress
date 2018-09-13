package io.jpress.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.model.User;
import io.jpress.service.PermissionService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class PermissionInterceptor implements Interceptor {


    private static final String NO_PERMISSION_VIEW = "/WEB-INF/views/admin/error/nopermission.html";

    @Inject
    private PermissionService permissionService;

    public void intercept(Invocation inv) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            inv.getController().render(NO_PERMISSION_VIEW);
            return;
        }

        if (!permissionService.hasPermission(user.getId(), inv.getActionKey())) {
            inv.getController().render(NO_PERMISSION_VIEW);
            return;
        }

        inv.invoke();
    }

}
