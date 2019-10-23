package io.jpress.web.front;

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
import io.jpress.core.payment.PaymentManager;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.commons.pay.PayConfigUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RequestMapping(value = "/pay")
public class PayController extends TemplateControllerBase {

    public static final String DEFAULT_ALIPAY_VIEW = "/WEB-INF/views/front/pay/pay_alipay.html";
    public static final String DEFAULT_ALIPAYX_VIEW = "/WEB-INF/views/front/pay/pay_alipayx.html";
    public static final String DEFAULT_WECHAT_VIEW = "/WEB-INF/views/front/pay/pay_wechat.html";
    public static final String DEFAULT_WECHAT_MOBILE_VIEW = "/WEB-INF/views/front/pay/pay_wechatmobile.html";
    public static final String DEFAULT_WECHATX_VIEW = "/WEB-INF/views/front/pay/pay_wechatx.html";

    @Inject
    private PaymentRecordService paymentService;

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

        setAttr("payConfig",PayConfigUtil.getWechatPayConfig());

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
        order.setTransactionType(WxTransactionType.JSAPI); //扫码付
        Map<String, Object> infos = service.orderInfo(order);

        setAttr("infos",infos);
        setAttr("payConfig",PayConfigUtil.getWechatPayConfig());

        render("pay_wechatmobile.html", DEFAULT_WECHAT_MOBILE_VIEW);
    }


    /**
     * 微信对私转账支付
     */
    public void wechatx() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment", payment);

        setAttr("payConfig",PayConfigUtil.getWechatxPayConfig());
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

        setAttr("payConfig",PayConfigUtil.getAlipayPayConfig());

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
        setAttr("payConfig",PayConfigUtil.getAlipayxPayConfig());
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


        String trxNo = String.valueOf(params.get("out_trade_no"));

        PaymentRecord payment = paymentService.findByTrxNo(trxNo);
        PaymentRecord oldPayment = payment.copy();


        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayStatus(PaymentRecord.PAY_STATUS_SUCCESS_ONLINE);
        payment.setPayCompleteTime(new Date());

        //微信支付
        //https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8
        if (service instanceof WxPayService) {
            payment.setPayBankType(String.valueOf(params.get("bank_type")));
            payment.setThirdpartyType("wechat");
            payment.setThirdpartyAppid(String.valueOf(params.get("appid")));
            payment.setThirdpartyMchId(String.valueOf(params.get("mch_id")));
            payment.setThirdpartyTradeType(String.valueOf(params.get("trade_type")));
            payment.setThirdpartyTransactionId(String.valueOf(params.get("transaction_id")));
            payment.setThirdpartyUserOpenid(String.valueOf(params.get("openid")));
        }

        //支付宝支付
        //https://open.alipay.com/developmentDocument.htm
        else if (service instanceof AliPayService) {
            payment.setThirdpartyType("alipay");
            payment.setThirdpartyAppid(String.valueOf(params.get("app_id")));
            payment.setThirdpartyMchId(String.valueOf(params.get("seller_id")));
//            payment.setThirdpartyTradeType(String.valueOf(params.get("trade_type")));
            payment.setThirdpartyTransactionId(String.valueOf(params.get("trade_no")));
            payment.setThirdpartyUserOpenid(String.valueOf(params.get("buyer_id")));
        }

        //Paypal支付
        else if (service instanceof PayPalPayService){
            payment.setThirdpartyType("paypal");
        }

        if (paymentService.update(payment)){
            PaymentManager.me().notifySuccess(oldPayment,paymentService.findById(payment.getId()));
        }

        renderText(service.getPayOutMessage("success", "成功").toMessage());
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


    /**
     * web 页面支付成功后跳转回的 url 地址
     */
    public void success() {
        redirect("/ucenter/order");
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
        return new PayOrder(
                payment.getProductTitle(),
                payment.getProductDesc(),
                payment.getPayAmount(),
                payment.getTrxNo());

    }


}
