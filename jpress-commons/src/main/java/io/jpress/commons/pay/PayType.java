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


public enum PayType {

    AMOUNT("amount", "余额支付"),
    WECHAT("wechat", "微信在线支付"),
    WECHATX("wechatx", "微信好友转账支付"),
    ALIPAY("alipay", "支付宝在线支付"),
    ALIPAYX("alipayx", "支付宝转账支付"),
    PAYPAL("paypal", "PayPal在线支付"),
    OFFLINE("offline", "线下支付"),
    OTHER("other", "其他方式支付"),
    UNKOWN("unkown", "未知");


    PayType(String type, String text) {
        this.type = type;
        this.text = text;
    }


    private String type;
    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static PayType getByType(String type) {
        for (PayType payType : values()) {
            if (payType.type.equals(type)) {
                return payType;
            }
        }
        return UNKOWN;
    }
}
