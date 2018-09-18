package io.jpress.module.page.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StringUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("pages")
public class PagesDirective extends JbootDirectiveBase {

    @Inject
    private SinglePageService singlePageService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getParam("flag", scope);
        if (StringUtils.isBlank(flag)) {
            throw new IllegalArgumentException("#pages(...) argument must not be empty ");
        }

        List<SinglePage> singlePages = singlePageService.findListByFlag(flag);
        scope.setLocal("pages", singlePages);

        renderBody(env, scope, writer);
    }
}
