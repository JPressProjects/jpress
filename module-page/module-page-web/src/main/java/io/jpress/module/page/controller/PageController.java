package io.jpress.module.page.controller;

import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller
 */
@RequestMapping("/page")
public class PageController extends TemplateControllerBase {

    @Inject
    private SinglePageService sps;

    public void index() {

        String target = StrUtils.urlDecode(JPressHandler.getCurrentTarget());
        String slug = target.substring(1);

        SinglePage page = sps.findFirstBySlug(slug);

        if (page == null || !page.isNormal()) {
            renderError(404);
            return;
        }

        setSeoTitle(page.getTitle());
        setSeoKeywords(page.getMetaKeywords());
        setSeoDescription(StrUtils.isBlank(page.getMetaDescription())
                ? CommonsUtils.maxLength(page.getText(), 100)
                : page.getMetaDescription());

        doFlagMenuActive(menu -> menu.getUrl().startsWith(target));

        setAttr("page", page);
        render(page.getHtmlView());
    }

}
