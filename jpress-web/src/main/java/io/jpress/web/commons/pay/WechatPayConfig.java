package io.jpress.web.commons.pay;


import com.egzosn.pay.wx.api.WxPayConfigStorage;
import io.jpress.JPressOptions;

public class WechatPayConfig {

    private boolean enable;
    private String mchId;
    private String appid;
    private String publicKey;
    private String privateKey;

    public WechatPayConfig() {
        setEnable(JPressOptions.getAsBool("wechat_pay_enable"));
        setMchId(JPressOptions.get("wechat_pay_mchId"));
        setAppid(JPressOptions.get("wechat_pay_appid"));
        setPublicKey(JPressOptions.get("wechat_pay_publicKey"));
        setPrivateKey(JPressOptions.get("wechat_pay_secretKey"));
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

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
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

    public WxPayConfigStorage toConfigStorage(){
        WxPayConfigStorage storage = new WxPayConfigStorage();
        storage.setMchId(getMchId());
        storage.setAppid(getAppid());
        storage.setKeyPublic(getPublicKey());
        storage.setKeyPrivate(getPrivateKey());

//        HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
//        httpConfigStorage.setKeystore(WxPayController.class.getResourceAsStream("/证书文件"));
//        httpConfigStorage.setStorePassword("证书密码");
//        //设置ssl证书对应的存储方式输入流，这里默认为文件地址
//        httpConfigStorage.setCertStoreType(CertStoreType.INPUT_STREAM);
//        service = new WxPayService(wxPayConfigStorage, httpConfigStorage);

        return storage;
    }

}
