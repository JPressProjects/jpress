package io.jpress.web.commons.pay;


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
}
