package io.jpress.web.front;

import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.paypal.bean.PayPalTransactionType;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.commons.pay.PayConfigUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@RequestMapping(value = "/pay", viewPath = "/WEB-INF/views/front/pay")
public class PayController extends TemplateControllerBase {

    @Inject
    private PaymentRecordService paymentService;

    /**
     * 获取异步通知的URL地址
     *
     * @return
     */
    private String getNotifyUrl() {
        return "";
    }

    /**
     * 获取web支付成功后跳转的url地址
     *
     * @return
     */
    private String getReturnUrl() {
        return "";
    }


    /**
     * 微信扫码支付
     */
    public void wechat() {

        PayService service = PayConfigUtil.getWxPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);

        order.setTransactionType(WxTransactionType.NATIVE); //扫码付
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(order);

    }

    /**
     * 微信手机调用js直接支付（无需扫码）
     */
    public void wechtmobile() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);
    }

    /**
     * 微信对私转账支付
     */
    public void wechatx() {
        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);
    }

    /**
     * 支付宝扫码支付
     */
    public void alipay() {
        PayService service =PayConfigUtil. getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);
        order.setTransactionType(AliTransactionType.SWEEPPAY); //扫码付
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(order);
    }

    /**
     * 支付宝网页登录支付（无需扫码）
     */
    public void alipayweb() {
        PayService service = PayConfigUtil.getAlipayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);
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
        setAttr("payment",payment);

//        PayOrder order = initOrderByPayment(payment);
    }

    /**
     * paypal 支付
     */
    public void paypal() {

        PayService service = PayConfigUtil.getPayPalPayService();
        render404If(service == null);

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);
        setAttr("payment",payment);

        PayOrder order = initOrderByPayment(payment);
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

        if (null == params || !service.verify(params)) {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
        } else {
            //这里处理业务逻辑
            renderText(service.getPayOutMessage("success", "成功").toMessage());
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


    /**
     * web 页面支付成功后跳转回的 url 地址
     */
    public void success() {
//        rendire
    }


    /**
     * 定时检查是否支付成功的地址
     */
    public void query() {
        PaymentRecord paymentRecord = paymentService.queryCacheByTrxno(getPara("trx"));
        if (paymentRecord!=null && paymentRecord.isPaySuccess()){
            renderOkJson();
        }else {
            renderFailJson();
        }
    }

    private PayOrder initOrderByPayment(PaymentRecord payment){
        return new PayOrder(
                payment.getProductName(),
                payment.getProductDesc(),
                payment.getPayAmount(),
                payment.getTrxNo());

    }



}
