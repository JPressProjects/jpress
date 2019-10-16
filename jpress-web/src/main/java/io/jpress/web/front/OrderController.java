package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.UserOrder;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;


@RequestMapping(value = "/ucenter/finance/order", viewPath = "/WEB-INF/views/ucenter/finance")
public class OrderController extends UcenterControllerBase {

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;

    /**
     * 用户订单列表
     */
    public void index() {
        Page<UserOrder> userOrderPage = orderService.paginateByUserId(getPagePara(), 10, getLoginedUser().getId(), getPara("title"), getPara("ns"));
        setAttr("userOrderPage", userOrderPage);
        render("order_list.html");
    }


    /**
     * 订单详情
     */
    public void detail() {
        UserOrder order = orderService.findById(getIdPara());
        if (order == null || order.getBuyerId() == null || !order.getBuyerId().equals(getLoginedUser().getId())) {
            renderError(404);
            return;
        }

        setAttr("order", order);
        setAttr("orderItems", orderItemService.findListByOrderId(order.getId()));
        setAttr("orderUser", userService.findById(order.getBuyerId()));
        setAttr("distUser", userService.findById(order.getDistUserId()));

        setAttr("order", order);
        render("order_detail.html");
    }


}
