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


import com.egzosn.pay.paypal.api.PayPalConfigStorage;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

public class PaypalPayConfig extends PayConfigBase{

    private boolean enable;
    private String clientId;
    private String clientSecret;

    public PaypalPayConfig() {
        super("paypal");
        setEnable(JPressOptions.getAsBool("paypal_pay_enable"));
        setClientId(JPressOptions.get("paypal_pay_clientid"));
        setClientSecret(JPressOptions.get("paypal_pay_clientsecret"));
    }

    public boolean isConfigOk() {
        return StrUtil.areNotEmpty(clientId, clientSecret);
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

    public PayPalConfigStorage toConfigStorage(){
        PayPalConfigStorage storage = new PayPalConfigStorage();
        storage.setClientID(getClientId());
        storage.setClientSecret(getClientSecret());
        storage.setNotifyUrl(getCallbackUrl());
        storage.setReturnUrl(getReturnUrl());
//        storage.setTest(true);
        return storage;
    }
}
