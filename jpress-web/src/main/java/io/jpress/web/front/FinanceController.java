package io.jpress.web.front;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.UcenterControllerBase;


@RequestMapping(value = "/ucenter/finance",viewPath = "/WEB-INF/views/ucenter/finance")
public class FinanceController extends UcenterControllerBase {

    /**
     * 用户订单
     */
    public void order(){
        render("orders.html");
    }

    /**
     * 用户余额信息
     */
    public void amount(){
        render("amount.html");
    }


}
