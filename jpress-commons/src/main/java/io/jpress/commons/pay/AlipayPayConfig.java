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


import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.common.util.sign.SignUtils;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

public class AlipayPayConfig extends PayConfigBase{

    private boolean enable;
    private String pid;
    private String appid;
    private String publicKey;
    private String privateKey;
    private String seller;


    public AlipayPayConfig() {
        super("alipay");
        setEnable(JPressOptions.getAsBool("alipay_pay_enable"));
        setPid(JPressOptions.get("alipay_pay_pid"));
        setAppid(JPressOptions.get("alipay_pay_appid"));
        setPublicKey(JPressOptions.get("alipay_pay_publicKey"));
        setPrivateKey(JPressOptions.get("alipay_pay_privateKey"));
        setSeller(JPressOptions.get("alipay_pay_seller"));
    }

    public boolean isConfigOk() {
        return StrUtil.areNotEmpty(pid, appid, publicKey, privateKey, seller);
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

    public AliPayConfigStorage toConfigStorage() {
        AliPayConfigStorage storage = new AliPayConfigStorage();
        storage.setPid(getPid());
        storage.setAppid(getAppid());
        storage.setKeyPublic(getPublicKey());
        storage.setKeyPrivate(getPrivateKey());
        storage.setSeller(getSeller());
        storage.setNotifyUrl(getCallbackUrl());
        storage.setReturnUrl(getReturnUrl());
        storage.setSignType(SignUtils.RSA2.name());
        storage.setInputCharset("utf-8");
        return storage;
    }
}
