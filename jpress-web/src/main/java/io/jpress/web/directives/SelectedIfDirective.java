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
@JFinalDirective("selectedIf")
public class SelectedIfDirective extends JbootDirectiveBase {


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Boolean param = getParam(0, scope);
        if (param == null) return;

        if (param == true) {
            try {
                writer.write("selected");
            } catch (IOException e) {
                throw new TemplateException(e.getMessage(), location, e);
            }
        }
    }
}

