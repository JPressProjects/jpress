package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.web.base.AdminControllerBase;

@RequestMapping(value = "/admin/preview",viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _PreviewController extends AdminControllerBase {

    public void index(){
        render("preview/preview.html");
    }
}
