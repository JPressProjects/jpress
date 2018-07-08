package io.jpress.core.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StringUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.service.OptionService;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("option")
public class OptionDirective extends JbootDirectiveBase {

    @Inject
    private OptionService optionService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String key = getParam(0, scope);
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("#option(...) argument must not be empty");
        }

        String value = optionService.findByKey(key);
        if (value == null) value = "";

        try {
            writer.write(value);
        } catch (IOException e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}

