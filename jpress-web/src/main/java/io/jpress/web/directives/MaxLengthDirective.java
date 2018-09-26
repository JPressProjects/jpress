package io.jpress.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("maxLength")
public class MaxLengthDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String content = getParam(0, scope);
        if (StrUtils.isBlank(content)) {
            return;
        }

        int maxLength = getParam(1, 0, scope);
        if (maxLength <= 0) {
            throw new IllegalArgumentException("#maxLength(content,length) 参数错误，length必须大于0 ");
        }

        if (content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }
        
        try {
            writer.write(content);
        } catch (IOException e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}

