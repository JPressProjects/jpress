package io.jpress.web.commons.pay;


import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.paypal.api.PayPalPayService;
import com.egzosn.pay.wx.api.WxPayService;

public class PayConfigUtil {

    public static AlipayPayConfig getAlipayPayConfig(){
        return new AlipayPayConfig();
    }

    public static AlipayxPayConfig getAlipayxPayConfig(){
        return new AlipayxPayConfig();
    }

    public static WechatPayConfig getWechatPayConfig(){
        return new WechatPayConfig();
    }

    public static WechatxPayConfig getWechatxPayConfig(){
        return new WechatxPayConfig();
    }

    public static PaypalPayConfig getPaypalPayConfig(){
        return new PaypalPayConfig();
    }


    public static WxPayService getWxPayService() {
        WechatPayConfig config = getWechatPayConfig();
        return config.isEnable() ? new WxPayService(config.toConfigStorage()) : null;
    }


    public static AliPayService getAlipayService() {
        AlipayPayConfig config = getAlipayPayConfig();
        return config.isEnable() ? new AliPayService(config.toConfigStorage()) : null;
    }


    public static PayPalPayService getPayPalPayService() {
        PaypalPayConfig config = getPaypalPayConfig();
        return config.isEnable() ? new PayPalPayService(config.toConfigStorage()) : null;
    }
}
