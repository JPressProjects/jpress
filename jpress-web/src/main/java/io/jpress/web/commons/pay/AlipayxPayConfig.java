package io.jpress.web.commons.pay;


import io.jpress.JPressOptions;

public class AlipayxPayConfig {

    private boolean enable;
    private String qrcode;
    private String message;

    public AlipayxPayConfig() {
        setEnable(JPressOptions.getAsBool("alipayx_pay_enable"));
        setQrcode(JPressOptions.get("alipayx_pay_qrcode"));
        setMessage(JPressOptions.get("alipayx_pay_message"));
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