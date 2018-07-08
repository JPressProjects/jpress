package io.jpress.core.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.core.menu.AdminMenuGroup;
import io.jpress.core.menu.AdminMenuManager;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class AdminInterceptor implements Interceptor {

    public void intercept(Invocation inv) {

        List<AdminMenuGroup> systemMenuGroups = AdminMenuManager.me().getSystemMenus();
        List<AdminMenuGroup> moduleMenuGroups = AdminMenuManager.me().getModuleMenus();

        inv.getController().setAttr("systemMenuGroups", systemMenuGroups);
        inv.getController().setAttr("moduleMenuGroups", moduleMenuGroups);

        inv.invoke();
    }
}
