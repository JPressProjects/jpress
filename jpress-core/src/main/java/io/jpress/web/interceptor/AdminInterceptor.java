package io.jpress.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConstants;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuManager;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 管理后台的拦截器
 * @Package io.jpress.web
 */
public class AdminInterceptor implements Interceptor {


    @Inject
    private UserService us;


    public void intercept(Invocation inv) {


        String uid = EncryptCookieUtils.get(inv.getController(), JPressConstants.COOKIE_UID);
        if (StrUtils.isBlank(uid)) {
            inv.getController().redirect("/admin/login");
            return;
        }

        User user = us.findById(uid);
        if (user == null || !user.isStatusOk()) {
            inv.getController().redirect("/admin/login");
            return;
        }

        List<MenuGroup> systemMenuGroups = MenuManager.me().getSystemMenus();
        List<MenuGroup> moduleMenuGroups = MenuManager.me().getModuleMenus();

        inv.getController().setAttr("systemMenuGroups", systemMenuGroups);
        inv.getController().setAttr("moduleMenuGroups", moduleMenuGroups);

        inv.getController().setAttr(JPressConstants.ATTR_LOGINED_USER, user);

        inv.invoke();
    }

}
