/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.commons.sms;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.sms
 */
public class SmsKit {

    /**
     * 发送验证码
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param template 短信模板
     * @param sign     短信签名
     * @return
     */
    public static boolean sendCode(String mobile, int code, String template, String sign) {

        SmsMessage sms = new SmsMessage();
        sms.setCode(code+"");
        sms.setSign(sign);
        sms.setMobile(mobile);
        sms.setTemplate(template);

        return sms.send();

    }
}
