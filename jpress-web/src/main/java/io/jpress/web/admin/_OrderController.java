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
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.service.UserService;
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


    @AdminMenu(text = "订单管理", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 1)
    public void index() {

        int todayOrderCount = orderService.queryTotayCount();
        int monthOrderCount = orderService.queryMonthCount();
        int mouthPaymentAmount = paymentService.queryMonthAmount();
        int mountOrderUserCount = orderService.queryMonthUserCount();

        setAttr("todayOrderCount",todayOrderCount);
        setAttr("monthOrderCount",monthOrderCount);
        setAttr("mouthPaymentAmount",mouthPaymentAmount);
        setAttr("mountOrderUserCount",mountOrderUserCount);


        Page<UserOrder> userOrderPage = orderService.paginate(getPagePara(), 10, getPara("title"), getPara("ns"));
        setAttr("page", userOrderPage);
        render("order/order_list.html");
    }


    public void detail() {
        UserOrder order = orderService.findById(getPara(),null);
        setAttr("order", order);
        setAttr("orderItems", orderItemService.findListByOrderId(order.getId()));
        setAttr("orderUser", userService.findById(order.getBuyerId()));
        setAttr("distUser", userService.findById(order.getDistUserId()));

        //如果快递已经发货
        if (order.isDeliveried()) {
            List<ExpressInfo> expressInfos = ExpressUtil.queryExpress(order.getDeliveryCompany(), order.getDeliveryNo());
            setAttr("expressInfos", expressInfos);
        }

        render("order/order_detail.html");
    }

    /**
     * 发货
     */
    public void deliver() {
        setAttr("order", orderService.findById(getPara(),null));
        setAttr("expressComs", ExpressCompany.EXPRESS_LIST);
        render("order/order_layer_deliver.html");
    }

    public void doUpdateDeliver() {
        UserOrder order = orderService.findById(getPara("orderId"),null);
        if (order == null) {
            renderFailJson();
        } else {

            int deliveryType = getParaToInt("deliveryType");

            order.setDeliveryStatus(UserOrder.DELIVERY_STATUS_DELIVERIED); //设置为已经发货
            order.setDeliveryType(deliveryType);
            order.setDeliveryNo(getPara("deliveryNo"));
            order.setDeliveryCompany(getPara("deliveryCompany"));
            order.setDeliveryStartTime(getParaToDate("deliveryStartTime"));


            if (UserOrder.DELIVERY_TYPE_NONEED == deliveryType){
                order.setTradeStatus(UserOrder.TRADE_STATUS_FINISHED);
            }else {
                order.setTradeStatus(UserOrder.TRADE_STATUS_COMPLETED);
            }

            List<UserOrderItem> orderItems = orderItemService.findListByOrderId(order.getId());
            for (UserOrderItem item : orderItems){
                item.setStatus(order.getTradeStatus());
            }

            orderService.update(order);
            renderOkJson();
        }
    }

    /**
     * 发票设置
     */
    public void invoice() {
        setAttr("order", orderService.findById(getPara(),null));
        render("order/order_layer_invoice.html");
    }

    public void doUpdateInvoice() {
        UserOrder order = orderService.findById(getPara("orderId"),null);
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
        setAttr("order", orderService.findById(getPara(),null));
        render("order/order_layer_remark.html");
    }

    public void doUpdateRemark() {
        UserOrder order = orderService.findById(getPara("orderId"),null);
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
        setAttr("order", orderService.findById(getPara(),null));
        render("order/order_layer_update_paystatus.html");
    }

    public void doUpdatePaystatus() {
        UserOrder order = orderService.findById(getPara("orderId"),null);
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
        setAttr("order", orderService.findById(getPara(),null));
        render("order/order_layer_update_price.html");
    }

    public void doUpdatePrice() {
        UserOrder order = orderService.findById(getPara("orderId"),null);
        if (order == null) {
            renderFailJson();
        } else {
            order.setOrderRealAmount(new BigDecimal(getPara("newPrice")));
            orderService.update(order);
            renderOkJson();
        }
    }


}
