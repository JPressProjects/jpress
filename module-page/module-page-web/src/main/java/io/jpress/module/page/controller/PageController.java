package io.jpress.module.page.controller;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;

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

        String slug = getSlug();

        if (StringUtils.isBlank(slug)) {
            render("page_index.html");
            return;
        }

        SinglePage page = sps.findFirstBySlug(slug);

        if (page == null || !page.isNormal()) {
            renderError(404);
            return;
        }

        setAttr("page", page);

        render("page_index.html");
    }

    private String getSlug() {
        String uri = getRequest().getRequestURI();
        return StringUtils.urlDecode(uri.substring(1, uri.length()));
    }
}
