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
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.commons.pay.PayType;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountStatement;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.commons.finance.PayNotifytKit;
import io.jpress.web.commons.finance.PrePayNotifytKit;
import io.jpress.web.interceptor.UserMustLoginedInterceptor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@RequestMapping(value = "/pay")
public class PayController extends TemplateControllerBase {

    public static final String DEFAULT_ALIPAY_VIEW = "/WEB-INF/views/front/pay/pay_alipay.html";
    public static final String DEFAULT_ALIPAYX_VIEW = "/WEB-INF/views/front/pay/pay_alipayx.html";
    public static final String DEFAULT_WECHAT_VIEW = "/WEB-INF/views/front/pay/pay_wechat.html";
    public static final String DEFAULT_WECHATX_VIEW = "/WEB-INF/views/front/pay/pay_wechatx.html";
    public static final String DEFAULT_FAIL_VIEW = "/WEB-INF/views/front/pay/pay_fail.html";
    public static final String DEFAULT_SUCCESS_VIEW = "/WEB-INF/views/front/pay/pay_success.html";
    public static final String DEFAULT_COMPLETE_VIEW = "/WEB-INF/views/front/pay/pay_complete.html";
    public static final String DEFAULT_ERROR_VIEW = "/WEB-INF/views/front/pay/pay_error.html";


    private static final Log LOG = Log.getLog(PayController.class);

    @Inject
    private PaymentRecordService paymentService;

    @Inject
    private UserService userService;

    @Inject
    private UserAmountStatementService statementService;


    /**
     * 微信扫码支付
     */
    public void wechat() {

        PayService service = PayConfigUtil.getWxPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        if (payment.isPaySuccess()){
            redirect("/pay/success/" + payment.getTrxNo());
            return;
        }

        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);

        order.setTransactionType(WxTransactionType.NATIVE); //扫码付
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(order);

        String base64Str = PayKit.image2base64Str(image);
        setAttr("qrcode_base64str", base64Str);

        setAttr("payConfig", PayConfigUtil.getWechatPayConfig());
        render("pay_wechat.html", DEFAULT_WECHAT_VIEW);

        PrePayNotifytKit.notify(payment, getLoginedUser());
    }

    /**
     * 微信手机调用js直接支付（无需扫码）
     * 前端通过 ajax 传入 trx 和 openId 调用此接口，得到返回数据后再调用如下 js
     * <p>
     * WeixinJSBridge.invoke(
     * 'getBrandWCPayRequest', {
     * "appId": data.orderInfo.appId,
     * "timeStamp": data.orderInfo.timeStamp,         //自1970年以来的秒数的时间戳
     * "nonceStr": data.orderInfo.nonceStr,           //随机串
     * "package": data.orderInfo.package,
     * "signType": data.orderInfo.signType,           //微信签名方式
     * "paySign": data.orderInfo.sign                 //微信签名
     * },function(res){
     * if(res.err_msg == "get_brand_wcpay_request:ok" ) {
     * alert("支付成功")
     * }
     * }
     * );
     */
    public void wechatmobile() {
        PayService service = PayConfigUtil.getWxPayService();
        if (service == null) {
            renderFailJson();
            return;
        }

        PaymentRecord payment = paymentService.findByTrxNo(getPara("trx"));
        if (payment == null || notLoginedUserModel(payment, "payer_user_id") || payment.isPaySuccess()) {
            renderFailJson();
            return;
        }


        String openId = getPara("openId");
        if (StrUtil.isBlank(openId)) {
            renderFailJson("openId is empty.");
            return;
        }

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(WxTransactionType.JSAPI);
        order.setOpenid(openId);

        renderJson(Ret.ok().set("orderInfo", service.orderInfo(order)));

        PrePayNotifytKit.notify(payment, getLoginedUser());
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

        PrePayNotifytKit.notify(payment, getLoginedUser());
    }

    /**
     * 支付宝扫码支付
     */
    public void alipay() {

        boolean qrcodePayEnable = JPressOptions.getAsBool("alipay_qrcode_pay_enable");
        if (!qrcodePayEnable) {
            redirect("/pay/alipayweb/" + getPara());
            return;
        }

        PayService service = PayConfigUtil.getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        if (payment.isPaySuccess()){
            redirect("/pay/success/" + payment.getTrxNo());
            return;
        }

        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);

        //设置扫码付
        order.setTransactionType(AliTransactionType.SWEEPPAY);

        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(order);

        String base64Str = PayKit.image2base64Str(image);
        setAttr("qrcode_base64str", base64Str);

        setAttr("payConfig", PayConfigUtil.getAlipayPayConfig());
        render("pay_alipay.html", DEFAULT_ALIPAY_VIEW);

        PrePayNotifytKit.notify(payment, getLoginedUser());
    }


    /**
     * 支付宝网页登录支付（无需扫码）
     */
    public void alipayweb() {

        PayService service = PayConfigUtil.getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        if (payment.isPaySuccess()){
            redirect("/pay/success/" + payment.getTrxNo());
            return;
        }

        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);

        // 手机浏览器
        if (isMobileBrowser()) {
            order.setTransactionType(AliTransactionType.WAP);
        }
        // PC 浏览器
        else {
            order.setTransactionType(AliTransactionType.PAGE);
        }


        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));

        PrePayNotifytKit.notify(payment, getLoginedUser());
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

        PrePayNotifytKit.notify(payment, getLoginedUser());
    }


    /**
     * paypal 支付
     */
    public void paypal() {
        PayService service = PayConfigUtil.getPayPalPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        if (payment.isPaySuccess()){
            redirect("/pay/success/" + payment.getTrxNo());
            return;
        }

        setAttr("payment", payment);

        PayOrder order = createPayOrder(payment);
        order.setTransactionType(PayPalTransactionType.sale); //电脑网页支付

        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));

        PrePayNotifytKit.notify(payment, getLoginedUser());
    }


    /**
     * 使用余额进行支付
     */
    public void amount() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null
                || notLoginedUserModel(payment, "payer_user_id")
                || PaymentRecord.TRX_TYPE_RECHARGE.equals(payment.getTrxType()));


        if (payment.isPaySuccess()){
            redirect("/pay/success/" + payment.getTrxNo());
            return;
        }


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
            paymentService.notifySuccess(payment.getId());
        }

        PayNotifytKit.notify(payment, getLoginedUser());

        redirect("/pay/success/" + payment.getTrxNo());

    }


    /**
     * 支付状态的异步回调地址
     */
    public void callback() {

        PayService service = getPayService();
        render404If(service == null);

        //获取支付方返回的对应参数
        Map<String, Object> params = getParams(service);
        if (Jboot.isDevMode()) {
            LOG.debug("callback : " + JSON.toJSONString(params));
        }

        //验证失败的原因：
        // 1 恶意 callback
        // 2 后台填写的公钥可能填写错了，或者不是支付宝公钥
        if (params == null || !service.verify(params)) {
            callbackFail(service);
            return;
        }

        String trxNo = getTrxNo(params);
        PaymentRecord payment = paymentService.findByTrxNo(trxNo);
        render404If(payment == null);

        // 已经支付成功，重复通知
        if (payment.isPaySuccess()) {
            renderText(service.getPayOutMessage("success", "成功").toMessage());
            return;
        }


        //微信支付
        //https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8
        if (service instanceof WxPayService) {
            //交易成功
            if ("SUCCESS".equals(params.get("result_code"))) {

                //total_fee 得到的单位是：分
                BigDecimal paySuccessAmount = new BigDecimal(params.get("total_fee").toString()).divide(BigDecimal.valueOf(100));
                payment.setPaySuccessAmount(paySuccessAmount);

                payment.setPayStatus(PayStatus.SUCCESS_WECHAT.getStatus());
                payment.setPayBankType(String.valueOf(params.get("bank_type")));
                payment.setThirdpartyType("wechat");
                payment.setThirdpartyAppid(String.valueOf(params.get("appid")));
                payment.setThirdpartyMchId(String.valueOf(params.get("mch_id")));
                payment.setThirdpartyTradeType(String.valueOf(params.get("trade_type")));
                payment.setThirdpartyTransactionId(String.valueOf(params.get("transaction_id")));
                payment.setThirdpartyUserOpenid(String.valueOf(params.get("openid")));

            } else {
                callbackFail(service);
                return;
            }

        }

        //支付宝支付
        //https://open.alipay.com/developmentDocument.htm
        //https://docs.open.alipay.com/api_1/alipay.trade.pay
        else if (service instanceof AliPayService) {
            //交易状态
            String trade_status = (String) params.get("trade_status");
            //交易完成
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {

                String paySuccessAmount = params.get("receipt_amount").toString();
                payment.setPaySuccessAmount(new BigDecimal(paySuccessAmount));

                payment.setPayStatus(PayStatus.SUCCESS_ALIPAY.getStatus());
                payment.setThirdpartyType("alipay");
                payment.setThirdpartyAppid(String.valueOf(params.get("app_id")));
                payment.setThirdpartyMchId(String.valueOf(params.get("seller_id")));
                payment.setThirdpartyTransactionId(String.valueOf(params.get("trade_no")));
                payment.setThirdpartyUserOpenid(String.valueOf(params.get("buyer_id")));
            } else {
                callbackFail(service);
                return;
            }
        }

        //Paypal支付
        //https://blog.csdn.net/weixin_42152023/article/details/93097326
        else if (service instanceof PayPalPayService) {

            if ("Completed".equals(params.get("payment_status"))) {
                payment.setPayStatus(PayStatus.SUCCESS_PAYPAL.getStatus());
                payment.setThirdpartyType("paypal");
                payment.setThirdpartyUserOpenid(getPara("payer_id"));
            } else {
                callbackFail(service);
                return;
            }
        }

        if (payment.getPaySuccessAmount() == null) {
            payment.setPaySuccessAmount(payment.getPayAmount());
        }

        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayCompleteTime(new Date());
        payment.setPaySuccessTime(new Date());


        if (paymentService.update(payment)) {
            paymentService.notifySuccess(payment.getId());
            renderText(service.getPayOutMessage("success", "成功").toMessage());
        } else {
            callbackFail(service);
        }

        PayNotifytKit.notify(payment, getLoginedUser());

    }

    private void callbackFail(PayService service) {
        renderText(service.getPayOutMessage("fail", "失败").toMessage());
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


    public void back() {

        PayService service = getPayService();
        render404If(service == null);

        Map<String, Object> params = getParams(service);
        if (Jboot.isDevMode()) {
            LOG.debug("back:" + JSON.toJSONString(params));
        }

        String trxNo = getTrxNo(params);

        if (params == null || !service.verify(params) || StrUtil.isBlank(trxNo)) {
            redirect("/pay/fail/" + trxNo);
            return;
        }

        PaymentRecord payment = paymentService.findByTrxNo(trxNo);

        if (payment.isPaySuccess()) {
            redirect("/pay/success/" + trxNo);
        } else {
            redirect("/pay/fail/" + trxNo);
        }

    }

    private Map<String, Object> getParams(PayService service) {
        try {
            return service.getParameter2Map(getRequest().getParameterMap(), getRequest().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * web 页面支付成功后跳转回的 url 地址
     */
    public void success() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null || !payment.isPaySuccess());

        setAttr("payment", payment);
        render("pay_success.html", DEFAULT_SUCCESS_VIEW);
    }


    public void fail() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("pay_fail.html", DEFAULT_FAIL_VIEW);
    }


    public void complete() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("pay_complete.html", DEFAULT_COMPLETE_VIEW);
    }


    /**
     * 支付错误页面，一般情况下后台为做正确的配置
     */
    public void error(){
        render("pay_error.html", DEFAULT_ERROR_VIEW);
    }


    /**
     * 定时检查是否支付成功的地址
     */
    @Before(UserMustLoginedInterceptor.class)
    public void query() {
        PaymentRecord paymentRecord = paymentService.queryCacheByTrxno(getPara("trx"));
        if (paymentRecord == null || notLoginedUserModel(paymentRecord, "payer_user_id") || !paymentRecord.isPaySuccess()) {
            renderFailJson();
        } else {
            renderOkJson();
        }
    }


    private PayOrder createPayOrder(PaymentRecord payment) {
        PayOrder payOrder = new PayOrder(
                tryTrim(payment.getProductTitle()),
                tryTrim(payment.getProductSummary()),
                payment.getPayAmount(),
                payment.getTrxNo());

        payOrder.setSpbillCreateIp(getIPAddress());
        return payOrder;
    }

    private String tryTrim(String string){
        return string == null ? null : string.trim();
    }


}
