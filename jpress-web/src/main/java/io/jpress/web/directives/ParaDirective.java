package io.jpress.web.directives;

import com.jfinal.core.Controller;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.service.RoleService;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("para")
public class ParaDirective extends JbootDirectiveBase {

    @Inject
    private RoleService roleService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();


        String key = getParam(0, scope);
        String defaultValue = getParam(1, scope);

        if (StrUtils.isBlank(key)) {
            throw new IllegalArgumentException("#para(...) argument must not be empty");
        }

        String value = controller.getPara(key);
        if (StrUtils.isBlank(value)) {
            value = StrUtils.isNotBlank(defaultValue) ? defaultValue : "";
        }


        try {
            writer.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

