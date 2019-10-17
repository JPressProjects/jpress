package io.jpress.web.commons.pay;


import io.jpress.JPressOptions;

public class AlipayPayConfig {

    private boolean enable;
    private String pid;
    private String appid;
    private String publicKey;
    private String privateKey;
    private String seller;

    public AlipayPayConfig() {
        setEnable(JPressOptions.getAsBool("alipay_pay_enable"));
        setPid(JPressOptions.get("alipay_pay_pid"));
        setAppid(JPressOptions.get("alipay_pay_appid"));
        setPublicKey(JPressOptions.get("alipay_pay_publicKey"));
        setPrivateKey(JPressOptions.get("alipay_pay_privateKey"));
        setSeller(JPressOptions.get("alipay_pay_seller"));
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
