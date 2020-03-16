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


import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.paypal.api.PayPalPayService;
import com.egzosn.pay.wx.api.WxPayService;
import com.jfinal.core.Controller;

public class PayConfigUtil {

    public static AlipayPayConfig getAlipayPayConfig() {
        return new AlipayPayConfig();
    }

    public static AlipayxPayConfig getAlipayxPayConfig() {
        return new AlipayxPayConfig();
    }

    public static WechatPayConfig getWechatPayConfig() {
        return new WechatPayConfig();
    }

    public static WechatxPayConfig getWechatxPayConfig() {
        return new WechatxPayConfig();
    }

    public static PaypalPayConfig getPaypalPayConfig() {
        return new PaypalPayConfig();
    }


    public static WxPayService getWxPayService() {
        WechatPayConfig config = getWechatPayConfig();
        return config.isEnable() && config.isConfigOk() ? new WxPayService(config.toConfigStorage()) : null;
    }


    public static AliPayService getAlipayService() {
        AlipayPayConfig config = getAlipayPayConfig();
        return config.isEnable() && config.isConfigOk() ? new AliPayService(config.toConfigStorage()) : null;
    }


    public static PayPalPayService getPayPalPayService() {
        PaypalPayConfig config = getPaypalPayConfig();
        return config.isEnable() && config.isConfigOk() ? new PayPalPayService(config.toConfigStorage()) : null;
    }

    public static void setConfigAttrs(Controller controller) {
        controller.setAttr("alipayConfig", getAlipayPayConfig());
        controller.setAttr("alipayxConfig", getAlipayxPayConfig());
        controller.setAttr("wechatConfig", getWechatPayConfig());
        controller.setAttr("wechatxConfig", getWechatxPayConfig());
        controller.setAttr("paypalConfig", getPaypalPayConfig());
    }
}
