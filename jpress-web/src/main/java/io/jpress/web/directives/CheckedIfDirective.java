package io.jpress.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("checkedIf")
public class CheckedIfDirective extends JbootDirectiveBase {


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Object param = getParam(0, scope);
        if (param == null || param.toString().trim().length() == 0) return;

        Boolean checked = param instanceof String ? "true".equalsIgnoreCase((String) param) : (Boolean) param;

        if (checked == true) {
            try {
                writer.write("checked");
            } catch (IOException e) {
                throw new TemplateException(e.getMessage(), location, e);
            }
        }
    }
}

