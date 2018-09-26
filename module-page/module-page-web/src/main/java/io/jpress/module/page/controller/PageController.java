package io.jpress.module.page.controller;

import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.TemplateControllerBase;

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

        if (StrUtils.isBlank(slug)) {
            render("page.html");
            return;
        }

        SinglePage page = sps.findFirstBySlug(slug);

        if (page == null || !page.isNormal()) {
            renderError(404);
            return;
        }

        setAttr("page", page);
        render(page.getHtmlView());
    }

    private String getSlug() {
        String uri = getRequest().getRequestURI();

        //可能已经启用了伪静态
        if (uri.contains(".")){
            uri = uri.substring(0,uri.lastIndexOf("."));
        }

        return StrUtils.urlDecode(uri.substring(1, uri.length()));
    }
}
