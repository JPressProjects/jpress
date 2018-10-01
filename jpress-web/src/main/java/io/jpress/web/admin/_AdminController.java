package io.jpress.web.admin;

import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import io.jboot.utils.EncryptCookieUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.JPressConstants;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin")
public class _AdminController extends AdminControllerBase {

    @Inject
    private UserService us;

    @Clear
    public void login() {
        render("login.html");
    }


    @Clear
    @EmptyValidate({
            @Form(name = "user", message = "账号不能为空"),
            @Form(name = "pwd", message = "密码不能为空")
    })
    public void doLogin(String user, String pwd) {

        Ret ret = StrUtils.isEmail(user)
                ? us.loginByEmail(user, pwd)
                : us.loginByUsername(user, pwd);

        if (ret.isOk()) {
            EncryptCookieUtils.put(this, JPressConstants.COOKIE_UID, ret.getLong("user_id"));
        }

        renderJson(ret);
    }

    @Clear
    public void logout() {
        EncryptCookieUtils.remove(this, JPressConstants.COOKIE_UID);
        redirect("/admin/login");
    }


    public void index() {

        List<String> moduleIncludes = new ArrayList<>();
        List<ModuleListener> listeners = ModuleManager.me().getListeners();
        for (ModuleListener listener : listeners) {
            String path = listener.onRenderDashboardBox(this);
            if (path == null) {
                continue;
            }

            if (path.startsWith("/")) {
                moduleIncludes.add(path);
            } else {
                moduleIncludes.add("/WEB-INF/views/admin/" + path);
            }
        }

        setAttr("moduleIncludes", moduleIncludes);
        render("index.html");
    }
}
