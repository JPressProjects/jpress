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


import io.jboot.utils.StrUtil;
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

    public boolean isConfigOk() {
        return StrUtil.areNotEmpty(qrcode, message);
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