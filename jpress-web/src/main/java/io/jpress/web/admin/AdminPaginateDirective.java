package io.jpress.web.admin;

import io.jboot.web.directive.JbootPaginateDirective;
import io.jboot.web.directive.annotation.JFinalDirective;

@JFinalDirective("adminPaginate")
public class AdminPaginateDirective extends JbootPaginateDirective {

    protected String getPageAttrName() {
        return "page";
    }
}
