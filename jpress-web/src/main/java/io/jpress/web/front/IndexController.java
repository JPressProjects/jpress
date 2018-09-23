package io.jpress.web.front;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/")
public class IndexController extends TemplateControllerBase {

    private static String style = null;

    public static void initStyle(String style) {
        IndexController.style = style;
    }


    public void index() {

        if (!"/".equals(getRequest().getRequestURI())) {
            forwardAction("/page");
        }


        String indexView = StringUtils.isNotBlank(style)
                ? "index_" + style + ".html"
                : "index.html";

        render(indexView);
    }
}
