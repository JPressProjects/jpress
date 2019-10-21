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

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.UrlParaValidate;
import io.jpress.model.*;
import io.jpress.service.*;
import io.jpress.web.base.UcenterControllerBase;
import io.jpress.web.commons.pay.PayConfigUtil;

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

    @Inject
    private PaymentRecordService paymentService;


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

        PayConfigUtil.setConfigAttrs(this);

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

        PayConfigUtil.setConfigAttrs(this);

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

            item.setViewText(userCart.getViewText());
            item.setViewPath(userCart.getViewPath());
            item.setViewEffectiveTime(userCart.getViewEffectiveTime());
            item.setCommentPath(userCart.getCommentPath());

            item.setOptions(userCart.getOptions());

            //payAmount = 产品价格 * 产品数量 + 运费 + 其他费用
            BigDecimal payAmount = userCart.getProductPrice()
                    .multiply(BigDecimal.valueOf(userCart.getProductCount()))
                    .add(item.getDeiveryCost())
                    .add(item.getOtherCost());

            item.setPayAmount(payAmount);

            //totalAmount = payamount - 分销金额
            item.setTotalAmount(payAmount.subtract(item.getDistAmount()));
            userOrderItems.add(item);
        }


        /**
         * 创建订单
         */
        UserOrder userOrder = getModel(UserOrder.class, "order");
        userOrder.setProductType(userOrderItems.get(0).getProductType());
        userOrder.setProductTitle(userOrderItems.get(0).getProductTitle());

        //只允许提交订单的如下字段，其他不允许提交
        userOrder.keep("delivery_addr_username","delivery_addr_mobile","delivery_addr_province",
                "delivery_addr_city","delivery_addr_district","delivery_addr_detail","delivery_addr_zipcode");

        userOrder.setBuyerId(getLoginedUser().getId());
        userOrder.setBuyerMsg(getPara("buyer_msg"));
        userOrder.setBuyerNickname(getLoginedUser().getNickname());
        userOrder.setNs(PayKit.genOrderNS());

        //设置订单的产品描述
        StringBuilder productDesc = new StringBuilder();
        for (UserOrderItem item : userOrderItems) {
            productDesc.append(item.getProductTitle());
        }
        if (productDesc.length() > 200){
            productDesc.delete(200,productDesc.length() -1);
            productDesc.append("...");
        }
        userOrder.setProductDesc(productDesc.toString());


        //设置优惠券的相关字段
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

        //设置 order item 的 order id
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

        PaymentRecord payment = paymentService.findById(userOrder.getPaymentId());
        if (payment == null){
            payment = new PaymentRecord();
        }

        payment.setProductTitle(userOrder.getProductTitle());
        payment.setProductType(userOrder.getProductType());
        payment.setProductRelativeId(userOrder.getId().toString());
        payment.setProductDesc(userOrder.getProductDesc());

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType(PaymentRecord.TRX_TYPE_ORDER);
        payment.setTrxNonceStr(StrUtil.uuid());

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);
        payment.setPayStatus(PaymentRecord.PAY_STATUS_PREPAY);//预支付

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(userOrder.getOrderRealAmount());
        payment.setPayStatus(PaymentRecord.PAY_STATUS_PREPAY);//预支付
        payment.setPayType(getPara("paytype"));


        payment.setStatus(PaymentRecord.STATUS_PAY_PRE); //预支付


        //保存 或 更新 payment
        paymentService.saveOrUpdate(payment);

        //更新 order 的 payment id
        if (userOrder.getPaymentId() == null){
            userOrder.setPaymentId(payment.getId());
            userOrderService.update(userOrder);
        }



        PayKit.redirect(payment.getPayType(), payment.getTrxNo());
    }


}
