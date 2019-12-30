package io.jpress.module.product.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping("/product/search")
public class ProductSearchController extends TemplateControllerBase {


    public void index() {

        /**
         * 不让页面大于100，我认为：
         * 1、当一个真实用户在搜索某个关键字的内容，通过翻页去找对应数据，不可能翻到100页以上。
         * 2、翻页翻到100页以上，一般是机器：可能是来抓取数据的。
         */
        int page = getParaToInt("page", 1);
        if (page <= 0 || page > 100) {
            renderError(404);
            return;
        }

        setAttr("keyword", getEscapeHtmlPara("keyword"));
        setAttr("page", page);

        setMenuActive(menu -> menu.isUrlStartWidth("/product/search"));
        render("prosearch.html");
    }

}
