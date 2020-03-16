/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.commons.pay;


import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

public class WechatPayConfig extends PayConfigBase {

    private boolean enable;
    private String mchId;
    private String appid;
    private String publicKey;
    private String privateKey;

    public WechatPayConfig() {
        super("wechat");
        setEnable(JPressOptions.getAsBool("wechat_pay_enable"));
        setMchId(JPressOptions.get("wechat_pay_mchId"));
        setAppid(JPressOptions.get("wechat_pay_appid"));
//        setPublicKey(JPressOptions.get("wechat_pay_publicKey"));
        setPrivateKey(JPressOptions.get("wechat_pay_secretKey"));
    }

    public boolean isConfigOk() {
        return StrUtil.areNotEmpty(mchId, appid, privateKey);
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

    public WxPayConfigStorage toConfigStorage() {
        WxPayConfigStorage storage = new WxPayConfigStorage();

        storage.setMchId(getMchId());
        storage.setAppid(getAppid());
//        storage.setKeyPublic(getPublicKey());
//        storage.setSecretKey(getPrivateKey());
        storage.setKeyPrivate(getPrivateKey());
        storage.setNotifyUrl(getCallbackUrl());
        storage.setReturnUrl(getReturnUrl());
        storage.setSignType(SignUtils.MD5.name());
        storage.setInputCharset("utf-8");

//        HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
//        httpConfigStorage.setKeystore(WxPayController.class.getResourceAsStream("/证书文件"));
//        httpConfigStorage.setStorePassword("证书密码");
//        //设置ssl证书对应的存储方式输入流，这里默认为文件地址
//        httpConfigStorage.setCertStoreType(CertStoreType.INPUT_STREAM);
//        service = new WxPayService(wxPayConfigStorage, httpConfigStorage);

        return storage;
    }

    @Override
    public String toString() {
        return "WechatPayConfig{" +
                "enable=" + enable +
                ", mchId='" + mchId + '\'' +
                ", appid='" + appid + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
