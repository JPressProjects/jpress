package io.jpress.web.directive.common;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 内容列表相关的指令
 * @Package io.jpress.directive
 */
@JFinalDirective("menus")
public class MenuListDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
