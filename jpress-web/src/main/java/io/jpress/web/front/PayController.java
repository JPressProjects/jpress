package io.jpress.web.front;

import com.alibaba.fastjson.JSON;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.paypal.api.PayPalPayService;
import com.egzosn.pay.paypal.bean.PayPalTransactionType;
import com.egzosn.pay.wx.api.WxPayService;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.commons.pay.PayType;
import io.jpress.core.payment.PaymentManager;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountStatement;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static io.jpress.web.commons.pay.MemberPaymentSuccessListener.LOG;

@RequestMapping(value = "/pay")
public class PayController extends TemplateControllerBase {

    public static final String DEFAULT_ALIPAY_VIEW = "/WEB-INF/views/front/pay/pay_alipay.html";
    public static final String DEFAULT_ALIPAYX_VIEW = "/WEB-INF/views/front/pay/pay_alipayx.html";
    public static final String DEFAULT_WECHAT_VIEW = "/WEB-INF/views/front/pay/pay_wechat.html";
    public static final String DEFAULT_WECHAT_MOBILE_VIEW = "/WEB-INF/views/front/pay/pay_wechatmobile.html";
    public static final String DEFAULT_WECHATX_VIEW = "/WEB-INF/views/front/pay/pay_wechatx.html";
    public static final String DEFAULT_FAIL_VIEW = "/WEB-INF/views/front/pay/pay_fail.html";
    public static final String DEFAULT_SUCCESS_VIEW = "/WEB-INF/views/front/pay/pay_success.html";

    @Inject
    private PaymentRecordService paymentService;

    @Inject
    private UserService userService;

    @Inject
    private UserAmountStatementService statementService;

    /**
     * 获取异步通知的URL地址
     *
     * @return
     */
    private String getNotifyUrl() {
        return "/pay/callback";
    }

    /**
     * 获取web支付成功后跳转的url地址
     *
     * @return
     */
    private String getReturnUrl() {
        return "/pay/success";
    }


    /**
     * 微信扫码支付
     */
    public void wechat() {

        PayService service = PayConfigUtil.getWxPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);

        order.setTransactionType(WxTransactionType.NATIVE); //扫码付
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(order);
        try {
            String base64Str = PayKit.image2base64Str(image);
            setAttr("qrcode_base64str", base64Str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setAttr("payConfig", PayConfigUtil.getWechatPayConfig());
        render("pay_wechat.html", DEFAULT_WECHAT_VIEW);
    }

    /**
     * 微信手机调用js直接支付（无需扫码）
     */
    public void wechatmobile() {
        PayService service = PayConfigUtil.getWxPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(WxTransactionType.JSAPI);
        Map<String, Object> infos = service.orderInfo(order);

        setAttr("infos", infos);
        setAttr("payConfig", PayConfigUtil.getWechatPayConfig());

        render("pay_wechatmobile.html", DEFAULT_WECHAT_MOBILE_VIEW);
    }


    /**
     * 微信对私转账支付
     */
    public void wechatx() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        setAttr("payConfig", PayConfigUtil.getWechatxPayConfig());
        render("pay_wechatx.html", DEFAULT_WECHATX_VIEW);
    }

    /**
     * 支付宝扫码支付
     */
    public void alipay() {
        PayService service = PayConfigUtil.getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(AliTransactionType.SWEEPPAY); //扫码付
        //获取扫码付的二维码

        BufferedImage image = service.genQrPay(order);

        try {
            String base64Str = PayKit.image2base64Str(image);
            setAttr("qrcode_base64str", base64Str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setAttr("payConfig", PayConfigUtil.getAlipayPayConfig());
        render("pay_alipay.html", DEFAULT_ALIPAY_VIEW);
    }

    /**
     * 支付宝网页登录支付（无需扫码）
     */
    public void alipayweb() {
        PayService service = PayConfigUtil.getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(AliTransactionType.PAGE); //电脑网页支付


        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));
    }

    /**
     * 支付宝对私转账支付
     */
    public void alipayx() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);
        setAttr("payConfig", PayConfigUtil.getAlipayxPayConfig());
        render("pay_alipayx.html", DEFAULT_ALIPAYX_VIEW);
    }

    /**
     * paypal 支付
     */
    public void paypal() {
        PayService service = PayConfigUtil.getPayPalPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(PayPalTransactionType.sale); //电脑网页支付

        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));
    }


    /**
     * 使用余额进行支付
     */
    public void amount() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        BigDecimal userAmount = userService.queryUserAmount(getLoginedUser().getId());
        if (userAmount == null || userAmount.compareTo(payment.getPayAmount()) < 0) {
            redirect("/pay/fail/" + getPara());
            return;
        }


        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayStatus(PayStatus.SUCCESS_AMOUNT.getStatus());
        payment.setPaySuccessAmount(payment.getPayAmount());
        payment.setPaySuccessTime(new Date());
        payment.setPayCompleteTime(new Date());
        payment.setPayType(PayType.AMOUNT.getType());


        UserAmountStatement statement = new UserAmountStatement();
        statement.setUserId(getLoginedUser().getId());

        statement.setAction(payment.getTrxType());
        statement.setActionDesc(payment.getTrxTypeStr());

        statement.setActionName("支付");
        statement.setActionRelativeType("payment_record");
        statement.setActionRelativeId(payment.getId());

        statement.setOldAmount(userAmount);
        statement.setChangeAmount(BigDecimal.ZERO.subtract(payment.getPayAmount()));
        statement.setNewAmount(userAmount.subtract(payment.getPayAmount()));

        statementService.save(statement);
        paymentService.update(payment);

        if (userService.updateUserAmount(getLoginedUser().getId(), userAmount,
                BigDecimal.ZERO.subtract(payment.getPayAmount()))) {
            PaymentManager.me().notifySuccess(paymentService.findById(payment.getId()));
        }

        redirect("/pay/success/" + payment.getTrxNo());

    }


    /**
     * 支付状态的异步回调地址
     */
    public void callback() {

        PayService service = getPayService();
        render404If(service == null);

        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = service.getParameter2Map(getRequest().getParameterMap(), getRequest().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //验证失败
        if (null == params || !service.verify(params)) {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
            return;
        }


        String trxNo = getTrxNo(params);
        PaymentRecord payment = paymentService.findByTrxNo(trxNo);


        //微信支付
        //https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8
        if (service instanceof WxPayService) {
            //交易成功
            if ("SUCCESS".equals(params.get("result_code"))) {
                payment.setPayStatus(PayStatus.SUCCESS_WECHAT.getStatus());
                payment.setPayBankType(String.valueOf(params.get("bank_type")));
                payment.setThirdpartyType("wechat");
                payment.setThirdpartyAppid(String.valueOf(params.get("appid")));
                payment.setThirdpartyMchId(String.valueOf(params.get("mch_id")));
                payment.setThirdpartyTradeType(String.valueOf(params.get("trade_type")));
                payment.setThirdpartyTransactionId(String.valueOf(params.get("transaction_id")));
                payment.setThirdpartyUserOpenid(String.valueOf(params.get("openid")));
            } else {
                renderText(service.getPayOutMessage("fail", "失败").toMessage());
                return;
            }

        }

        //支付宝支付
        //https://open.alipay.com/developmentDocument.htm
        else if (service instanceof AliPayService) {
            //交易状态
            String trade_status = (String) params.get("trade_status");
            //交易完成
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {


                payment.setPayStatus(PayStatus.SUCCESS_ALIPAY.getStatus());
                payment.setThirdpartyType("alipay");
                payment.setThirdpartyAppid(String.valueOf(params.get("app_id")));
                payment.setThirdpartyMchId(String.valueOf(params.get("seller_id")));
//            payment.setThirdpartyTradeType(String.valueOf(params.get("trade_type")));
                payment.setThirdpartyTransactionId(String.valueOf(params.get("trade_no")));
                payment.setThirdpartyUserOpenid(String.valueOf(params.get("buyer_id")));
            } else {
                renderText(service.getPayOutMessage("fail", "失败").toMessage());
                return;
            }
        }

        //Paypal支付
        //https://blog.csdn.net/weixin_42152023/article/details/93097326
        else if (service instanceof PayPalPayService) {

//            if ("Completed".equals(params.get("payment_status")) && "verified".equals(params.get("payer_status"))){
            if ("Completed".equals(params.get("payment_status"))) {
                payment.setPayStatus(PayStatus.SUCCESS_PAYPAL.getStatus());
                payment.setThirdpartyType("paypal");
                payment.setThirdpartyUserOpenid(getPara("payer_id"));
            } else {
                renderText(service.getPayOutMessage("fail", "失败").toMessage());
                return;
            }
        }

        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayCompleteTime(new Date());
        payment.setPaySuccessTime(new Date());
        payment.setPaySuccessAmount(payment.getPayAmount());//此处不严谨，需要从返回值中获取

        if (paymentService.update(payment)) {
            PaymentManager.me().notifySuccess(paymentService.findById(payment.getId()));
            renderText(service.getPayOutMessage("success", "成功").toMessage());
        } else {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
        }

    }


    private PayService getPayService() {
        switch (getPara()) {
            case "wechat":
                return PayConfigUtil.getWxPayService();
            case "alipay":
                return PayConfigUtil.getAlipayService();
            case "paypal":
                return PayConfigUtil.getPayPalPayService();
            default:
                return null;
        }
    }

    private String getTrxNo(Map<String, Object> params) {
        return getPayService() instanceof PayPalPayService
                ? getPara("invoice")
                : String.valueOf(params.get("out_trade_no"));
    }


    public void back() throws IOException {

        PayService service = getPayService();
        render404If(service == null);

        Map<String, String[]> parameterMap = getRequest().getParameterMap();
        InputStream is = getRequest().getInputStream();

        Map<String, Object> params = service.getParameter2Map(parameterMap, is);
        if (LOG.isDebugEnabled()) {
            LOG.debug("回调响应:" + JSON.toJSONString(params));
        }

        String trxNo = getTrxNo(params);

        if (!service.verify(params)) {
            redirect("/pay/fail/" + trxNo);
            return;
        }


        //支付宝支付
        if (service instanceof AliPayService) {

            //交易状态
            String trade_status = (String) params.get("trade_status");

            //交易完成
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                redirect("/pay/success/" + trxNo);
                return;
            }
        }

        //微信支付
        else if (service instanceof WxPayService) {
            if ("SUCCESS".equals(params.get("result_code"))) {
                redirect("/pay/success/" + trxNo);
                return;
            }
        }

        //paypal 支付
        else if (service instanceof PayPalPayService) {

//            if ("Completed".equals(params.get("payment_status")) && "verified".equals(params.get("payer_status"))){
            if ("Completed".equals(params.get("payment_status"))) {
                redirect("/pay/success/" + trxNo);
                return;
            }
        }

        redirect("/pay/fail/" + trxNo);

    }


    /**
     * web 页面支付成功后跳转回的 url 地址
     */
    public void success() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        setAttr("payment", payment);
        render("pay_success.html", DEFAULT_SUCCESS_VIEW);
    }


    public void fail() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        setAttr("payment", payment);
        render("pay_fail.html", DEFAULT_FAIL_VIEW);
    }


    /**
     * 定时检查是否支付成功的地址
     */
    public void query() {
        PaymentRecord paymentRecord = paymentService.queryCacheByTrxno(getPara("trx"));
        if (paymentRecord != null && paymentRecord.isPaySuccess()) {
            renderOkJson();
        } else {
            renderFailJson();
        }
    }


    private PayOrder createPayOrder(PaymentRecord payment) {
        PayOrder payOrder = new PayOrder(
                payment.getProductTitle(),
                payment.getProductSummary(),
                payment.getPayAmount(),
                payment.getTrxNo());

        payOrder.setSpbillCreateIp(getIPAddress());
        return payOrder;
    }


}
