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


import org.apache.commons.lang3.StringUtils;

/**
 * @author michael
 */

public enum PayStatus {


    /**
     * 未支付 或者 准备支付
     */
    UNPAY(1, "未支付"),

    /**
     * 支付失败
     */
    FAILURE(2, "支付失败"),


    /**
     * 支付中，但是未支付成功，由用户进行标识
     */
    PAID_ALIPAYX(3, "支付宝转账中"),
    PAID_WECHATX(4, "微信转账中"),
    PAID_OFFLINE(5, "线下支付中"),
    PAID_OTHER(6, "其他方式支付中"),


    /**
     * 以下都是支付成功的情况
     */
    SUCCESS_ALIPAY(10, "支付宝支付成功"),
    SUCCESS_ALIPAYX(11, "支付宝转账成功"),
    SUCCESS_WECHAT(12, "微信支付成功"),
    SUCCESS_WECHATX(13, "微信转账成功"),
    SUCCESS_AMOUNT(14, "余额支付成功"),
    SUCCESS_PAYPAL(15, "PayPal支付成功"),
    SUCCESS_OFFLINE(16, "下线支付成功"),
    SUCCESS_OTHER(17, "其他方式支付成功");

    /**
     * 状态
     */
    private int status;

    /**
     * 文本内容
     */
    private String text;

    PayStatus(int status, String text) {
        this.status = status;
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static PayStatus getByStatus(int status) {
        for (PayStatus payStatus : values()) {
            if (payStatus.status == status) {
                return payStatus;
            }
        }
        return null;
    }

    public static PayStatus getSuccessStatusByType(PayType payType) {
        switch (payType) {
            case ALIPAY:
                return SUCCESS_ALIPAY;
            case ALIPAYX:
                return SUCCESS_ALIPAYX;
            case WECHAT:
                return SUCCESS_WECHAT;
            case WECHATX:
                return SUCCESS_WECHATX;
            case AMOUNT:
                return SUCCESS_AMOUNT;
            case PAYPAL:
                return SUCCESS_PAYPAL;
            case OFFLINE:
                return SUCCESS_OFFLINE;
            case OTHER:
                return SUCCESS_OTHER;
            default:
                return null;
        }
    }


    public static int getSuccessIntStatusByType(String type) {
        PayStatus payStatus = PayStatus.getSuccessStatusByType(PayType.getByType(type));
        return payStatus.getStatus();
    }


    public static PayStatus getPaidStatusByType(PayType payType) {
        switch (payType) {
            case ALIPAYX:
                return PAID_ALIPAYX;
            case WECHATX:
                return PAID_WECHATX;
            case OFFLINE:
                return PAID_OFFLINE;
            case OTHER:
                return PAID_OTHER;
            default:
                return null;
        }
    }

    public static String getTextByInt(Integer status) {
        if (status == null) {
            return StringUtils.EMPTY;
        }
        PayStatus payStatus = getByStatus(status);
        return payStatus != null ? payStatus.text : StringUtils.EMPTY;
    }


}
