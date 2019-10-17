package io.jpress.web.commons.pay;


import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.paypal.api.PayPalConfigStorage;
import com.egzosn.pay.paypal.api.PayPalPayService;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
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

        WxPayConfigStorage storage = getWechatPayConfig().toConfigStorage();
        //https证书设置，退款必须 方式二
/*
    HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    httpConfigStorage.setKeystore(WxPayController.class.getResourceAsStream("/证书文件"));
    httpConfigStorage.setStorePassword("证书密码");
    //设置ssl证书对应的存储方式输入流，这里默认为文件地址
    httpConfigStorage.setCertStoreType(CertStoreType.INPUT_STREAM);
    service = new WxPayService(wxPayConfigStorage, httpConfigStorage);
*/
        return new WxPayService(storage);
    }


    public static AliPayService getAlipayService() {

        AliPayConfigStorage storage = getAlipayPayConfig().toConfigStorage();
        //是否为测试账号，沙箱环境
//        aliPayConfigStorage.setTest(true);

        return new AliPayService(storage);
    }


    public static PayPalPayService getPayPalPayService() {

        PayPalConfigStorage storage = getPaypalPayConfig().toConfigStorage();

        return new PayPalPayService(storage);
    }
}
