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

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("page")
public class PageDirective extends JbootDirectiveBase {

    @Inject
    private SinglePageService singlePageService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String slug = getParam("slug", scope);
        if (StringUtils.isBlank(slug)) {
            throw new IllegalArgumentException("#page(...) argument must not be empty ");
        }

        SinglePage page = singlePageService.findFirstBySlug(slug);
        scope.setLocal("page", page);

        renderBody(env, scope, writer);
    }


}
