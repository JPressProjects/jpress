/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.finance.OrderManager;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.CouponCode;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderDelivery;
import io.jpress.model.UserOrderItem;
import io.jpress.service.*;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.express.ExpressCompany;
import io.jpress.web.commons.express.ExpressInfo;
import io.jpress.web.commons.express.ExpressUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/order", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _OrderController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_OrderController.class);

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;

    @Inject
    private PaymentRecordService paymentService;

    @Inject
    private UserOrderDeliveryService deliveryService;

    @Inject
    private CouponCodeService couponCodeService;


    @AdminMenu(text = "订单管理", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 1)
    public void index() {

        int todayOrderCount = orderService.queryTotayCount();
        int monthOrderCount = orderService.queryMonthCount();
        int mouthPaymentAmount = paymentService.queryMonthAmount();
        int mountOrderUserCount = orderService.queryMonthUserCount();

        setAttr("todayOrderCount", todayOrderCount);
        setAttr("monthOrderCount", monthOrderCount);
        setAttr("mouthPaymentAmount", mouthPaymentAmount);
        setAttr("mountOrderUserCount", mountOrderUserCount);

        keepPara();
        Page<UserOrder> userOrderPage = orderService.paginate(getPagePara(), 10, getPara("productTitle"), getPara("ns"));
        setAttr("page", userOrderPage);
        render("order/order_list.html");
    }


    public void detail() {
        UserOrder order = orderService.findById(getPara());

        List<UserOrderItem> orderItems = orderItemService.findListByOrderId(order.getId());

        setAttr("order", order);
        setAttr("orderItems", orderItems);
        setAttr("orderUser", userService.findById(order.getBuyerId()));

        if (orderItems != null) {
            for (UserOrderItem item : orderItems) {
                item.put("distUser", userService.findById(item.getId()));
                item.put("totalDistAmount", item.getDistAmount() == null ? 0 : item.getDistAmount().multiply(BigDecimal.valueOf(item.getProductCount())));
            }
        }


        //如果快递已经发货
        if (order.isDeliveried()) {
            UserOrderDelivery delivery = deliveryService.findById(order.getDeliveryId());
            List<ExpressInfo> expressInfos = ExpressUtil.queryExpress(delivery.getCompany(), delivery.getNumber());
            setAttr("expressInfos", expressInfos);
        }

        if (StrUtil.isNotBlank(order.getCouponCode())) {
            CouponCode orderCoupon = couponCodeService.findByCode(order.getCouponCode());
            if (orderCoupon != null) {
                setAttr("orderCoupon", orderCoupon);
                setAttr("orderCouponUser", userService.findById(orderCoupon.getUserId()));
            }
        }

        render("order/order_detail.html");
    }

    /**
     * 发货
     */
    public void deliver() {
        setAttr("order", orderService.findById(getPara()));
        setAttr("expressComs", ExpressCompany.values());
        render("order/order_layer_deliver.html");
    }

    /**
     * 更新发货信息
     */
    public void doUpdateDeliver() {
        UserOrder order = orderService.findById(getPara("orderId"));
        if (order == null) {
            renderFailJson();
            return;

        }

        // 发货的类型
        int deliveryType = getParaToInt("deliveryType");


        //不是无需发货，需要生成发货信息
        if (UserOrder.DELIVERY_TYPE_NONEED != deliveryType) {

            UserOrderDelivery delivery = new UserOrderDelivery();

            delivery.setNumber(getPara("deliveryNo"));
            delivery.setCompany(getPara("deliveryCompany"));
            delivery.setStartTime(getParaToDate("deliveryStartTime"));

            delivery.setAddrUsername(order.getDeliveryAddrUsername());
            delivery.setAddrMobile(order.getDeliveryAddrMobile());
            delivery.setAddrProvince(order.getDeliveryAddrProvince());
            delivery.setAddrCity(order.getDeliveryAddrCity());
            delivery.setAddrDistrict(order.getDeliveryAddrDistrict());
            delivery.setAddrDetail(order.getDeliveryAddrDetail());
            delivery.setAddrZipcode(order.getDeliveryAddrZipcode());

            Object deliveryId = deliveryService.save(delivery);
            if (deliveryId != null) {
                order.setDeliveryId((Long) deliveryId);
            }
        }


        //设置订单的相关发货信息
        order.setDeliveryType(deliveryType);


        //无需发货，直接设置订单为结束
        if (UserOrder.DELIVERY_TYPE_NONEED == deliveryType) {
            order.setTradeStatus(UserOrder.TRADE_STATUS_FINISHED);
            order.setDeliveryStatus(UserOrder.DELIVERY_STATUS_NONEED);
        }
        // 需要发货，设置订单状态为完成
        else {
            order.setTradeStatus(UserOrder.TRADE_STATUS_COMPLETED);
            order.setDeliveryStatus(UserOrder.DELIVERY_STATUS_DELIVERIED);
        }

        //设置订单项的相关发货信息
        List<UserOrderItem> orderItems = orderItemService.findListByOrderId(order.getId());
        for (UserOrderItem item : orderItems) {

            //如果是虚拟产品，设置该订单项的状态为完成
            if (item.isVirtualProduct()) {
                item.setStatus(UserOrderItem.STATUS_FINISHED);
            }
            //否则设置订单项的状态和订单的状态相同
            else {
                item.setStatus(order.getTradeStatus());
            }
        }

        //保存订单以及订单项的发货信息
        if (orderService.updateOrderAndItems(order, orderItems)) {
            for (UserOrderItem item : orderItems) {
                OrderManager.me().notifyItemStatusChanged(item);
            }
        }

        OrderManager.me().notifyOrderStatusChanged(order);
        renderOkJson();
    }

    /**
     * 发票设置
     */
    public void invoice() {
        setAttr("order", orderService.findById(getPara()));
        render("order/order_layer_invoice.html");
    }

    public void doUpdateInvoice() {
        UserOrder order = orderService.findById(getPara("orderId"));
        if (order == null) {
            renderFailJson();
        } else {
            order.setInvoiceStatus(getParaToInt("invoiceStatus"));
            orderService.update(order);
            renderOkJson();
        }
    }

    /**
     * 备注设置
     */
    public void remark() {
        setAttr("order", orderService.findById(getPara()));
        render("order/order_layer_remark.html");
    }

    public void doUpdateRemark() {
        UserOrder order = orderService.findById(getPara("orderId"));
        if (order == null) {
            renderFailJson();
        } else {
            order.setRemarks(getPara("remarks"));
            orderService.update(order);
            renderOkJson();
        }
    }

    /**
     * 手动入账
     */
    public void updatePaystatus() {
        setAttr("order", orderService.findById(getPara()));
        render("order/order_layer_update_paystatus.html");
    }

    public void doUpdatePaystatus() {
        UserOrder order = orderService.findById(getPara("orderId"));
        if (order == null) {
            renderFailJson();
        } else {
            order.setPayStatus(getParaToInt("payStatus"));
            order.setPaySuccessAmount(new BigDecimal(getPara("paidAmount")));
            order.setPaySuccessTime(getParaToDate("paidTime"));
            order.setPaySuccessProof(getPara("paidProof"));
            order.setPaySuccessRemarks(getPara("paidRemarks"));
            orderService.update(order);
            renderOkJson();
        }
    }

    /**
     * 修改价格
     */
    public void updatePrice() {
        setAttr("order", orderService.findById(getPara()));
        render("order/order_layer_update_price.html");
    }

    public void doUpdatePrice() {
        UserOrder order = orderService.findById(getPara("orderId"));
        if (order == null) {
            renderFailJson();
        } else {
            order.setOrderRealAmount(new BigDecimal(getPara("newPrice")));
            orderService.update(order);
            renderOkJson();
        }
    }


}
