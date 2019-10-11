package io.jpress.web.front;

import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.paypal.api.PayPalConfigStorage;
import com.egzosn.pay.paypal.api.PayPalPayService;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.web.base.TemplateControllerBase;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setMchId(JPressOptions.get("wechat_pay_mchId"));//合作者id（商户号）
        wxPayConfigStorage.setAppid(JPressOptions.get("wechat_pay_appId")); //应用id
        wxPayConfigStorage.setKeyPublic(JPressOptions.get("wechat_pay_publicKey")); //转账公钥，转账时必填
        wxPayConfigStorage.setSecretKey(JPressOptions.get("wechat_pay_SecretKey")); //密钥
        wxPayConfigStorage.setNotifyUrl(getNotifyUrl()); //异步回调地址
        wxPayConfigStorage.setReturnUrl(getReturnUrl()); //同步回调地址
        wxPayConfigStorage.setInputCharset("utf-8");

        //https证书设置，退款必须 方式一
/*
    HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    httpConfigStorage.setKeystore("证书信息串");
    httpConfigStorage.setStorePassword("证书密码");
    //设置ssl证书对应的存储方式，这里默认为文件地址
    httpConfigStorage.setCertStoreType(CertStoreType.PATH);
    PayService service = new WxPayService(wxPayConfigStorage, httpConfigStorage);
*/

        PayService service = new WxPayService(wxPayConfigStorage);
        PayOrder payOrder = new PayOrder(
                "订单title",
                "摘要",
                new BigDecimal(0.01),
                StrUtil.uuid());

        payOrder.setTransactionType(WxTransactionType.NATIVE); //扫码付
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(payOrder);

    }

    /**
     * 微信手机调用js直接支付（无需扫码）
     */
    public void wechtmobile() {

    }

    /**
     * 微信对私转账支付
     */
    public void wechatx() {

    }

    /**
     * 支付宝扫码支付
     */
    public void alipay() {
        AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
        aliPayConfigStorage.setPid("合作者id");
        aliPayConfigStorage.setAppid("应用id");
        aliPayConfigStorage.setKeyPublic("支付宝公钥");
        aliPayConfigStorage.setKeyPrivate("应用私钥");
        aliPayConfigStorage.setNotifyUrl("异步回调地址");
        aliPayConfigStorage.setReturnUrl("同步回调地址");
        aliPayConfigStorage.setSignType("签名方式");
        aliPayConfigStorage.setSeller("收款账号");
        aliPayConfigStorage.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfigStorage.setTest(true);

        PayService service = new AliPayService(aliPayConfigStorage);

        PayOrder payOrder = new PayOrder("订单title", "摘要", new BigDecimal(0.01), UUID.randomUUID().toString().replace("-", ""));

        /*-----------扫码付-------------------*/
        payOrder.setTransactionType(AliTransactionType.SWEEPPAY);
        //获取扫码付的二维码
        BufferedImage image = service.genQrPay(payOrder);
    }

    /**
     * 支付宝网页登录支付（无需扫码）
     */
    public void alipayweb() {
        AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
        aliPayConfigStorage.setPid("合作者id");
        aliPayConfigStorage.setAppid("应用id");
        aliPayConfigStorage.setKeyPublic("支付宝公钥");
        aliPayConfigStorage.setKeyPrivate("应用私钥");
        aliPayConfigStorage.setNotifyUrl("异步回调地址");
        aliPayConfigStorage.setReturnUrl("同步回调地址");
        aliPayConfigStorage.setSignType("签名方式");
        aliPayConfigStorage.setSeller("收款账号");
        aliPayConfigStorage.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfigStorage.setTest(true);

        PayService service = new AliPayService(aliPayConfigStorage);

        PayOrder order = new PayOrder("订单title", "摘要", new BigDecimal(0.01), UUID.randomUUID().toString().replace("-", ""));

        //WAP支付
        //order.setTransactionType(AliTransactionType.WAP);
        //电脑网页支付
        order.setTransactionType(AliTransactionType.PAGE);


        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));
    }

    /**
     * 支付宝对私转账支付
     */
    public void alipayx() {

    }

    /**
     * paypal 支付
     */
    public void paypal() {
        PayPalConfigStorage storage = new PayPalConfigStorage();
        storage.setClientID("商户id");
        storage.setClientSecret("商户密钥");
        storage.setTest(true);
        //发起付款后的页面转跳地址
        storage.setReturnUrl("http://127.0.0.1:8088/pay/success");
        //取消按钮转跳地址,这里用异步通知地址的兼容的做法
        storage.setNotifyUrl("http://127.0.0.1:8088/pay/cancel");

        PayService service = new PayPalPayService(storage);

        PayOrder order = new PayOrder("订单title", "摘要", new BigDecimal(0.01), UUID.randomUUID().toString().replace("-", ""));

        //WAP支付
        //order.setTransactionType(AliTransactionType.WAP);
        //电脑网页支付
        order.setTransactionType(AliTransactionType.PAGE);


        //获取支付订单信息
        Map orderInfo = service.orderInfo(order);
        //组装成html表单信息
        renderHtml(service.buildRequest(orderInfo, MethodType.POST));
    }


    /**
     * 支付状态的异步回调地址
     */
    public void callback() {
        String type = getPara();
        if ("wechat".equals(type)) {
            doProcessWechatCallback();
        }else if ("alipay".equals("type")){
            doProcessAlipayCallback();
        }else if ("paypal".equals("type")){
            doProcessPaypalCallback();
        }
    }

    private void doProcessWechatCallback()  {

        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setMchId("合作者id（商户号）");
        wxPayConfigStorage.setAppid("应用id");
        wxPayConfigStorage.setKeyPublic("转账公钥，转账时必填");
        wxPayConfigStorage.setSecretKey("密钥");
        wxPayConfigStorage.setNotifyUrl("异步回调地址");
        wxPayConfigStorage.setReturnUrl("同步回调地址");
        wxPayConfigStorage.setSignType("签名方式");
        wxPayConfigStorage.setInputCharset("utf-8");
        //https证书设置，退款必须 方式二
/*
    HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    httpConfigStorage.setKeystore(WxPayController.class.getResourceAsStream("/证书文件"));
    httpConfigStorage.setStorePassword("证书密码");
    //设置ssl证书对应的存储方式输入流，这里默认为文件地址
    httpConfigStorage.setCertStoreType(CertStoreType.INPUT_STREAM);
    service = new WxPayService(wxPayConfigStorage, httpConfigStorage);
*/
        PayService service = new WxPayService(wxPayConfigStorage);

        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = service.getParameter2Map(getRequest().getParameterMap(), getRequest().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == params || !service.verify(params)) {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
        }else {
            //这里处理业务逻辑
            renderText(service.getPayOutMessage("success", "成功").toMessage());
        }

    }


    private void doProcessAlipayCallback()  {

        AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
        aliPayConfigStorage.setPid("合作者id");
        aliPayConfigStorage.setAppid("应用id");
        aliPayConfigStorage.setKeyPublic("支付宝公钥");
        aliPayConfigStorage.setKeyPrivate("应用私钥");
        aliPayConfigStorage.setNotifyUrl("异步回调地址");
        aliPayConfigStorage.setReturnUrl("同步回调地址");
        aliPayConfigStorage.setSignType("签名方式");
        aliPayConfigStorage.setSeller("收款账号");
        aliPayConfigStorage.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfigStorage.setTest(true);

        PayService service = new AliPayService(aliPayConfigStorage);

        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = service.getParameter2Map(getRequest().getParameterMap(), getRequest().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == params || !service.verify(params)) {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
        }else {
            //这里处理业务逻辑
            renderText(service.getPayOutMessage("success", "成功").toMessage());
        }

    }


    private void doProcessPaypalCallback()  {

        PayPalConfigStorage storage = new PayPalConfigStorage();
        storage.setClientID("商户id");
        storage.setClientSecret("商户密钥");
        storage.setTest(true);
        //发起付款后的页面转跳地址
        storage.setReturnUrl("http://127.0.0.1:8088/pay/success");
        //取消按钮转跳地址,这里用异步通知地址的兼容的做法
        storage.setNotifyUrl("http://127.0.0.1:8088/pay/cancel");

        PayService service = new PayPalPayService(storage);

        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = service.getParameter2Map(getRequest().getParameterMap(), getRequest().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == params || !service.verify(params)) {
            renderText(service.getPayOutMessage("fail", "失败").toMessage());
        }else {
            //这里处理业务逻辑
            renderText(service.getPayOutMessage("success", "成功").toMessage());
        }

    }


    /**
     * web 页面支付成功后跳转回的url 地址
     */
    public void success() {

    }


    /**
     * 定时检查是否支付成功的地址
     */
    public void query() {

    }

}
