package io.jpress.web.admin.directive;

import io.jboot.web.directive.JbootPaginateDirective;
import io.jboot.web.directive.annotation.JFinalDirective;

@JFinalDirective("adminPaginate")
public class _PaginateDirective extends JbootPaginateDirective {

    protected String getPageAttrName() {
        return "page";
    }
}
