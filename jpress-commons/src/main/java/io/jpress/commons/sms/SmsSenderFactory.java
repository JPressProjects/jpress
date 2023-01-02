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


import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

public class SmsSenderFactory {


    public static SmsSender createSender() {

        boolean smsEnable = JPressOptions.getAsBool(JPressConsts.OPTION_CONNECTION_SMS_ENABLE);

        if (smsEnable == false) {
            return new NonSmsSender();
        }

        String type = JPressOptions.get(JPressConsts.OPTION_CONNECTION_SMS_TYPE);

        // 默认使用阿里云
        if (StrUtil.isBlank(type)) {
            return new AliyunSmsSender();
        }

        switch (type) {
            case JPressConsts.SMS_TYPE_ALIYUN:
                return new AliyunSmsSender();
            case JPressConsts.SMS_TYPE_QCLOUD:
                return new QCloudSmsSender();
        }

        //其他通过SPI扩展机制加载
        return JbootSpiLoader.load(SmsSender.class, type);
    }

}
