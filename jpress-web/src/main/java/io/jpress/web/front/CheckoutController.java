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
package io.jpress.web.front;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.UrlParaValidate;
import io.jpress.model.*;
import io.jpress.service.*;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping(value = "/ucenter/checkout", viewPath = "/WEB-INF/views/ucenter/checkout")
public class CheckoutController extends UcenterControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private UserCartService cartService;

    @Inject
    private UserAddressService addressService;

    @Inject
    private CouponCodeService couponCodeService;

    @Inject
    private CouponService couponService;

    @Inject
    private UserOrderService userOrderService;

    @Inject
    private UserOrderItemService userOrderItemService;


    /**
     * 购买页面
     */
    public void index() {
        List<UserCart> userCarts = new ArrayList<>();
        Long cid = getParaToLong();
        if (cid != null) {
            userCarts.add(cartService.findById(cid));
        } else {
            userCarts.addAll(cartService.findSelectedByUserId(getLoginedUser().getId()));
        }

        setAttr("userCarts", userCarts);
        setAttr("defaultAddress", addressService.findDefaultAddress(getLoginedUser().getId()));

        render("checkout.html");
    }



    public void order() {
        UserOrder order = userOrderService.findById(getPara());
        if (order == null || order.getBuyerId() == null || !order.getBuyerId().equals(getLoginedUser().getId())) {
            renderError(404);
            return;
        }

        setAttr("order",order);
        setAttr("orderItems",userOrderItemService.findListByOrderId(order.getId()));
        setAttr("defaultAddress", addressService.findDefaultAddress(getLoginedUser().getId()));
        render("order.html");
    }


    /**
     * 开始购买
     */
    public void genorder() {

        String[] cids = getParaValues("cid");
        if (cids == null || cids.length == 0) {
            renderFailJson();
            return;
        }

        /**
         * 创建订单项
         */
        List<UserOrderItem> userOrderItems = new ArrayList<>(cids.length);
        for (String cid : cids) {
            UserCart userCart = cartService.findById(cid);

            UserOrderItem item = new UserOrderItem();
//            item.setBuyerMsg();
            item.setBuyerId(userCart.getUserId());
            item.setBuyerNickname(getLoginedUser().getNickname());
            item.setSellerId(userCart.getSellerId());

            item.setProductId(userCart.getProductId());
            item.setProductType(userCart.getProductType());
            item.setProductTitle(userCart.getProductTitle());
            item.setProductPrice(userCart.getProductPrice());
            item.setProductCount(userCart.getProductCount());
            item.setProductThumbnail(userCart.getProductThumbnail());

            item.setDistAmount(BigDecimal.ZERO); //分销金额
            item.setDeiveryCost(BigDecimal.ZERO);//运费，后台设置
            item.setOtherCost(BigDecimal.ZERO); //其他费用

            //payAmount = 产品价格 * 产品数量 + 运费 + 其他费用
            BigDecimal payAmount = userCart.getProductPrice()
                    .multiply(BigDecimal.valueOf(userCart.getProductCount()))
                    .add(item.getDeiveryCost())
                    .add(item.getOtherCost())
                    .subtract(item.getDistAmount());

            item.setPayAmount(payAmount);

            //totalAmount = payamount - 分销金额
            item.setTotalAmount(payAmount.subtract(item.getDistAmount()));

            userOrderItems.add(item);
        }


        /**
         * 创建订单
         */
        UserOrder userOrder = getModel(UserOrder.class, "order");
        userOrder.setBuyerId(getLoginedUser().getId());
        userOrder.setBuyerMsg(getPara("buyer_msg"));
        userOrder.setBuyerNickname(getLoginedUser().getNickname());
        userOrder.setNs(PayKit.genOrderNS());
        String codeStr = getPara("coupon_code");
        if (StrUtil.isNotBlank(codeStr)) {
            CouponCode couponCode = couponCodeService.findByCode(codeStr);
            if (couponCode == null || !couponCodeService.valid(couponCode)) {
                renderJson(Ret.fail().set("message", "优惠码不可用"));
                return;
            }


            Coupon coupon = couponService.findById(couponCode.getCouponId());

            //设置优惠码
            userOrder.setCouponCode(codeStr);
            userOrder.setCouponAmount(coupon.getAmount());
        }

        //保存 order
        Long userOrderId = (Long) userOrderService.save(userOrder);
        for (UserOrderItem item : userOrderItems) {
            item.setOrderId(userOrderId);
            item.setOrderNs(userOrder.getNs());
        }
        //保存 order item
        userOrderItemService.batchSave(userOrderItems);

        //订单金额 = 所有 item 金额之和 - 优惠券金额
        BigDecimal orderTotalAmount = new BigDecimal(0);
        for (UserOrderItem item : userOrderItems) {
            orderTotalAmount = orderTotalAmount.add(item.getPayAmount());
        }

        if (userOrder.getCouponAmount() != null) {
            orderTotalAmount = orderTotalAmount.subtract(userOrder.getCouponAmount());
        }

        userOrder.setOrderTotalAmount(orderTotalAmount);
        userOrder.setOrderRealAmount(orderTotalAmount);
        userOrder.setId(userOrderId);

        userOrder.setPayStatus(UserOrder.PAY_STATUS_UNPAY);//支付状态：未支付
        userOrder.setTradeStatus(UserOrder.TRADE_STATUS_TRADING);//交易状态：交易中...
        userOrder.setDeliveryStatus(UserOrder.DELIVERY_STATUS_UNDELIVERY);//发货状态：未发货
        userOrder.setInvoiceStatus(UserOrder.INVOICE_STATUS_NOT_APPLY);//发票开具状态：用户未申请

        userOrderService.update(userOrder);

//        cartService.deleteById()

        renderJson(Ret.ok().set("orderId", userOrderId).set("paytype", getPara("paytype")));

    }

    @UrlParaValidate
    public void payorder() {
        UserOrder userOrder = userOrderService.findById(getPara());
        render404If(userOrder == null);


        PaymentRecord payment = new PaymentRecord();
        payment.setProductName(userOrder.getTitle());
        payment.setProductType("product");

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType(PaymentRecord.TRX_TYPE_ORDER);

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);
        payment.setPayStatus(PaymentRecord.PAY_STATUS_PREPAY);//预支付

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(userOrder.getOrderRealAmount());
        payment.setStatus(PaymentRecord.STATUS_PAY_PRE); //预支付

        PaymentRecordService paymentService = Aop.get(PaymentRecordService.class);

        //保存 payment
        paymentService.save(payment);

        PayKit.redirect(getPara("paytype"), payment.getTrxNo());
    }


}
