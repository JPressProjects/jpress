package io.jpress.directive.content;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 内容相关的指令
 * @Package io.jpress.directive
 */
@JFinalDirective("url")
public class ContentDirective extends JbootDirectiveBase {

    @Override
    public void exec(Env env, Scope scope, Writer writer) {

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
