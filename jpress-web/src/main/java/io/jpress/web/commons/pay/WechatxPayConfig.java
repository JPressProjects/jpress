package io.jpress.web.commons.pay;


import io.jpress.JPressOptions;

public class WechatxPayConfig {

    private boolean enable;
    private String qrcode;
    private String message;

    public WechatxPayConfig() {
        setEnable(JPressOptions.getAsBool("wechatx_pay_enable"));
        setQrcode(JPressOptions.get("wechatx_pay_qrcode"));
        setMessage(JPressOptions.get("wechatx_pay_message"));
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
