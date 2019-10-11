package io.jpress.web.front;

import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping(value = "/pay", viewPath = "/WEB-INF/views/front/pay")
public class PayController extends TemplateControllerBase {

    @Inject
    private PaymentRecordService paymentService;


    public void index() {
        String trxNo = getPara();
        PaymentRecord payment = paymentService.findByTrxNo(trxNo);
        render404If(payment == null);

        String payType = getPara("type");

        boolean initSuccess = initPayInfo(payType, payment);
        render404If(!initSuccess);

        render(payType + ".html");
    }

    private boolean initPayInfo(String payType, PaymentRecord payment) {
        switch (payType) {
            case "wechat":
                return initWechatInfo(payment);
            case "wechatx":
                return initWechatxInfo(payment);
            case "alipay":
                return initAlipayInfo(payment);
            case "alipayx":
                return initAlipayxInfo(payment);
            case "paypal":
                return initPaypalInfo(payment);
            default:
                return false;
        }
    }



    private boolean initWechatInfo(PaymentRecord payment) {

        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setMchId(JPressOptions.get("wechat_pay_mchId"));//合作者id（商户号）
        wxPayConfigStorage.setAppid(JPressOptions.get("wechat_pay_appId")); //应用id
        wxPayConfigStorage.setKeyPublic(JPressOptions.get("wechat_pay_publicKey")); //转账公钥，转账时必填
        wxPayConfigStorage.setSecretKey(JPressOptions.get("wechat_pay_SecretKey")); //密钥
        wxPayConfigStorage.setNotifyUrl(""); //异步回调地址
        wxPayConfigStorage.setReturnUrl(""); //同步回调地址
        wxPayConfigStorage.setSignType("");  //签名方式
        wxPayConfigStorage.setInputCharset("utf-8");

        return false;
    }

    private boolean initWechatxInfo(PaymentRecord payment) {
        return false;
    }



    private boolean initAlipayInfo(PaymentRecord payment) {
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
        return false;
    }

    private boolean initAlipayxInfo(PaymentRecord payment) {
        return false;
    }

    private boolean initPaypalInfo(PaymentRecord payment) {
        return false;
    }



    /**
     * 定时检查是否支付成功的地址
     */
    public void query() {

    }


    /**
     *  支付状态的异步回调地址
     */
    public void callback() {
        renderText("callback");
    }

    /**
     * web 页面支付成功后跳转回的url 地址
     */
    public void success(){

    }

}
