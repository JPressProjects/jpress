package io.jpress.web.commons.pay;


import io.jpress.JPressOptions;

public class PaypalPayConfig {

    private boolean enable;
    private String clientId;
    private String clientSecret;

    public PaypalPayConfig() {
        setEnable(JPressOptions.getAsBool("paypal_pay_enable"));
        setClientId(JPressOptions.get("paypal_pay_clientid"));
        setClientSecret(JPressOptions.get("paypal_pay_clientsecret"));
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
