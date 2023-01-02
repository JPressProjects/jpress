/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.Jboot;
import io.jpress.commons.utils.CommonsUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.sms
 */
public class SmsKit {

    private static final String CACHE_NAME = "sms_code";

    /**
     * 发送短信
     *
     * @param mobile   手机号
     * @param template 短信模板
     * @param sign     短信签名
     * @return
     */
    public static boolean sendSms(String mobile, String template, String sign) {
        SmsMessage sms = new SmsMessage();
        sms.setSign(sign);
        sms.setMobile(mobile);
        sms.setTemplate(template);
        return sms.send();
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param code
     * @param template
     * @param sign
     * @return
     */
    public static boolean sendCode(String mobile, String code, String template, String sign) {

        SmsMessage sms = new SmsMessage();
        sms.setCode(code);
        sms.setSign(sign);
        sms.setMobile(mobile);
        sms.setTemplate(template);

        if (sms.send()) {
            //有效期，2个小时
            Jboot.getCache().put(CACHE_NAME, mobile, code, 60 * 60 * 2);
            return true;
        }
        return false;
    }

    /**
     * 验证用户输入的手机号是否正确
     *
     * @param mobile
     * @param code
     * @return
     */
    public static boolean validateCode(String mobile, String code) {
        String cacheCode = Jboot.getCache().get(CACHE_NAME, mobile);
        return cacheCode != null && cacheCode.equals(code);
    }


    /**
     * 生成一个四位数字的码
     *
     * @return
     */
    public static String generateCode() {
        return CommonsUtils.generateCode();
    }
}
