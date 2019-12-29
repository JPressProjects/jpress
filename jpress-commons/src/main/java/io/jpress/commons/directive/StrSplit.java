package io.jpress.commons.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.util.Set;

@JFinalDirective("strSplit")
public class StrSplit extends JbootDirectiveBase {
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String orginalStr = getPara(0, scope);
        if (StrUtil.isBlank(orginalStr)) {
            return;
        }

        String splitStr = getPara(1, scope, ",");
        Set<String> strs = StrUtil.splitToSet(orginalStr,splitStr);

        scope.setLocal("strs", strs);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
