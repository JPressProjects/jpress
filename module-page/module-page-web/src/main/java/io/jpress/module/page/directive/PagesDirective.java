package io.jpress.module.page.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressConsts;
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
        if (StrUtils.isBlank(flag)) {
            throw new IllegalArgumentException("#pages(...) argument must not be empty ");
        }

        List<SinglePage> singlePages = singlePageService.findListByFlag(flag);
        if (singlePages == null || singlePages.isEmpty()) {
            return;
        }

        doFlagIsActiveByCurrentPage(singlePages);
        scope.setLocal("pages", singlePages);

        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }

    private void doFlagIsActiveByCurrentPage(List<SinglePage> pages) {
        SinglePage currentPage = JbootControllerContext.get().getAttr("page");

        //当前url并不是页面详情
        if (currentPage == null) {
            return;
        }

        for (SinglePage page : pages) {
            if (page.equals(currentPage)) {
                JPressConsts.doFlagModelActive(page);
            }
        }
    }
}
