package io.jpress.core.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.User;
import io.jpress.service.PermissionService;
import io.jpress.core.web.base.UserInterceptor;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("permission")
public class PermissionDirective extends JbootDirectiveBase {

    @Inject
    private PermissionService permissionService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null || !user.isStatusOk()) {
            return;
        }


        String permission = getParam(0, scope);
        if (permission == null || permission.trim().length() == 0) {
            throw new IllegalArgumentException("#permission(...) argument must not be empty");
        }

        if (permissionService.hasPermission(user.getId(), permission)) {
            renderBody(env, scope, writer);
        }
    }
}

