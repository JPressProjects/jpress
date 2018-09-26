/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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


import io.jboot.utils.StrUtils;

public class SmsSenderFactory {


    public static ISmsSender createSender() {

        String provider = "sms_app_provider";// OptionQuery.me().findValue("sms_app_provider");

        if (StrUtils.isBlank(provider)) {
            return new AlidayuSmsSender();
        } else if ("sms_provider_alidayu".equals(provider)) {
            return new AlidayuSmsSender();
        }

//		其他短信服务商
//		else if("sms_provider_xxx".equals(provider)){
//			return new XXXSmsSender();
//		}

        return new AlidayuSmsSender();

    }

}
