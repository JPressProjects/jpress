package io.jpress.module.page.controller;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.annotation.NeedAuthentication;
import io.jpress.web.base.ApiControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/api/page")
public class PageApiController extends ApiControllerBase {


    public void index() {
        renderJson(Ret.ok());
    }

    @NeedAuthentication
    public void add() {

    }

    @NeedAuthentication
    public void delete() {

    }

    @NeedAuthentication
    public void update() {

    }

}
