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


    /**
     * 微信扫码支付
     */
    public void wechat(){

        PaymentRecord payment = paymentService.findByTrxNo(getPara());
        render404If(payment == null);

        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setMchId(JPressOptions.get("wechat_pay_mchId"));//合作者id（商户号）
        wxPayConfigStorage.setAppid(JPressOptions.get("wechat_pay_appId")); //应用id
        wxPayConfigStorage.setKeyPublic(JPressOptions.get("wechat_pay_publicKey")); //转账公钥，转账时必填
        wxPayConfigStorage.setSecretKey(JPressOptions.get("wechat_pay_SecretKey")); //密钥
        wxPayConfigStorage.setNotifyUrl(""); //异步回调地址
        wxPayConfigStorage.setReturnUrl(""); //同步回调地址
        wxPayConfigStorage.setSignType("");  //签名方式
        wxPayConfigStorage.setInputCharset("utf-8");

    }

    /**
     * 微信手机调用js直接支付（无需扫码）
     */
    public void wechtmobile(){

    }

    /**
     * 微信对私转账支付
     */
    public void wechatx(){

    }

    /**
     * 支付宝扫码支付
     */
    public void alipay(){
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
    }

    /**
     * 支付宝网页登录支付（无需扫码）
     */
    public void alipayweb(){

    }

    /**
     * 支付宝对私转账支付
     */
    public void alipayx(){

    }

    /**
     * paypal 支付
     */
    public void paypal(){
        render( "paypal.html");
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
