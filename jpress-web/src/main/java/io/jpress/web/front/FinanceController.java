package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.UserOrder;
import io.jpress.service.UserOrderService;
import io.jpress.web.base.UcenterControllerBase;


@RequestMapping(value = "/ucenter/finance", viewPath = "/WEB-INF/views/ucenter/finance")
public class FinanceController extends UcenterControllerBase {

    @Inject
    private UserOrderService orderService;


    /**
     * 用户订单列表
     */
    public void order() {
        render("order_list.html");
    }


    /**
     * 订单详情
     */
    @ActionKey("/ucenter/finance/order/detail")
    public void detail() {
        UserOrder order = orderService.findById(getIdPara());
        if (order == null || order.getBuyerId() == null || !order.getBuyerId().equals(getLoginedUser().getId())) {
            renderError(404);
            return;
        }

        setAttr("order", order);
        render("order_detail.html");
    }

    /**
     * 用户余额信息
     */
    public void amount() {
        render("amount.html");
    }


    /**
     * 金额充值页面
     */
    @ActionKey("/ucenter/finance/amount/recharge")
    public void recharge() {
        render("recharge.html");
    }

    /**
     * 支付
     */
    @ActionKey("/ucenter/finance/amount/doRecharge")
    public void doRecharge(){

    }

    /**
     * 支付页面
     */
    @ActionKey("/ucenter/finance/amount/recharge/pay")
    public void pay() {
        render("recharge.html");
    }


}
