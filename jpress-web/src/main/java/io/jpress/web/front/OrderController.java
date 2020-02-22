package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.finance.OrderManager;
import io.jpress.core.finance.ProductManager;
import io.jpress.model.*;
import io.jpress.service.*;
import io.jpress.web.base.UcenterControllerBase;
import io.jpress.web.commons.express.ExpressInfo;
import io.jpress.web.commons.express.ExpressUtil;

import java.util.Date;
import java.util.List;


@RequestMapping(value = "/ucenter/order", viewPath = "/WEB-INF/views/ucenter/order")
public class OrderController extends UcenterControllerBase {

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;

    @Inject
    private CouponCodeService couponCodeService;

    @Inject
    private UserOrderDeliveryService deliveryService;

    @Inject
    private UserOrderInvoiceService invoiceService;

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
        render404If(notLoginedUserModel(order, "buyer_id"));

        List<UserOrderItem> orderItems = orderItemService.findListByOrderId(order.getId());

        setAttr("order", order);
        setAttr("orderItems", orderItems);
        setAttr("orderUser", userService.findById(order.getBuyerId()));
        setAttr("invoice", invoiceService.findById(order.getInvoiceId()));
        setAttr("delivery", deliveryService.findById(order.getDeliveryId()));


        if (orderItems != null) {
            for (UserOrderItem item : orderItems) {
                item.put("optionsMap", ProductManager.me().renderProductOptions(item));
            }
        }

        if (StrUtil.isNotBlank(order.getCouponCode())) {
            CouponCode couponCode = couponCodeService.findByCode(order.getCouponCode());
            setAttr("orderCoupon", couponCode);
            setAttr("orderCouponUser", userService.findById(couponCode.getUserId()));
        }

        //如果快递已经发货
        if (order.isDeliveried()) {
            UserOrderDelivery delivery = deliveryService.findById(order.getDeliveryId());
            if (delivery != null) {
                List<ExpressInfo> expressInfos = ExpressUtil.queryExpress(delivery.getCompany(), delivery.getNumber());
                setAttr("expressInfos", expressInfos);
            }
        }


        setAttr("order", order);

        render("order_detail.html");
    }

    public void comment() {
        UserOrderItem item = orderItemService.findById(getPara());
        render404If(notLoginedUserModel(item, "buyer_id"));

        redirect(item.getCommentPath() + "?productId=" + item.getProductId() + "&orderItemId=" + item.getId());
    }


    public void addMessage() {
        UserOrder userOrder = orderService.findById(getPara());
        render404If(notLoginedUserModel(userOrder, "buyer_id"));

        setAttr("order", userOrder);
        render("order_layer_addmessage.html");
    }


    public void doAddMessage() {
        UserOrder userOrder = orderService.findById(getPara("orderId"));
        render404If(notLoginedUserModel(userOrder, "buyer_id"));

        userOrder.setBuyerMsg(getPara("message"));
        orderService.saveOrUpdate(userOrder);
        renderOkJson();
    }


    public void applyForInvoice(){
        UserOrder userOrder = orderService.findById(getPara());
        render404If(notLoginedUserModel(userOrder, "buyer_id"));

        setAttr("order",userOrder);
        render("order_layer_invoice.html");
    }


    public void doApplyForInvoice(){

        UserOrder userOrder = orderService.findById(getPara("orderId"));
        render404If(notLoginedUserModel(userOrder, "buyer_id"));

        UserOrderInvoice invoice = getModel(UserOrderInvoice.class,"invoice");
        invoice.setStatus(UserOrderInvoice.INVOICE_STATUS_APPLYING); //设置状态为申请中


        Long id = (Long) invoiceService.save(invoice);
        userOrder.setInvoiceId(id);
        userOrder.setInvoiceStatus(UserOrder.INVOICE_STATUS_APPLYING);

        orderService.update(userOrder);

        renderOkJson();
    }

    /**
     * 用户在用户中心确认收货
     */
    public void doFlagDelivery() {
        UserOrder userOrder = orderService.findById(getPara());
        render404If(notLoginedUserModel(userOrder, "buyer_id"));

        if (userOrder.isDeliverFinished()) {
            renderOkJson();
            return;
        }

        userOrder.setDeliveryStatus(UserOrder.DELIVERY_STATUS_FINISHED);
        UserOrderDelivery delivery = deliveryService.findById(userOrder.getDeliveryId());
        if (delivery != null) {
            delivery.setFinishTime(new Date());
            delivery.setStatus(UserOrderDelivery.STATUS_FINISHED);
            deliveryService.update(delivery);
        }

        boolean needNotifyStatusChaned = false;
        if (!userOrder.isFinished()) {
            userOrder.setTradeStatus(UserOrder.TRADE_STATUS_FINISHED);
            needNotifyStatusChaned = true;
        }

        if (orderService.update(userOrder) || needNotifyStatusChaned) {
            OrderManager.me().notifyOrderStatusChanged(userOrder);
        }


        renderOkJson();
    }


}
