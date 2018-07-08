package io.jpress.module.page.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.web.base.FrontControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.module.page.controller
 */
@RequestMapping("/page")
public class IndexController extends FrontControllerBase {

    public void index() {
        System.out.println(getRequest().getRequestURI());
        renderText("page");
    }
}
