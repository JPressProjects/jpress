package io.jpress.web.front;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuManager;
import io.jpress.web.base.UserInterceptor;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户中心的拦截器，用户中心要求用户必须登录
 * 该拦截器应该放在 UserInterceptor 之后执行
 * @Package io.jpress.web
 */
public class UserCenterInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

        if (UserInterceptor.getThreadLocalUser() == null) {
            inv.getController().redirect("/user/login");
            return;
        }

        List<MenuGroup> ucenterMenus = MenuManager.me().getUcenterMenus();
        inv.getController().setAttr("ucenterMenus", ucenterMenus);

        inv.invoke();

    }

}
