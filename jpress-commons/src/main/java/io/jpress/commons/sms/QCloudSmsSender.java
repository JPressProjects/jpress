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

import com.jfinal.log.Log;

/**
 * 腾讯云短信发送
 * api 接口文档 ：https://cloud.tencent.com/document/product/382/5976
 */
public class QCloudSmsSender implements ISmsSender {
    private static final Log log = Log.getLog(QCloudSmsSender.class);


    @Override
    public boolean send(SmsMessage sms) {

        String app_key = "your app key";
        String app_secret = "your app secret";

        return false;
    }


    public static void main(String[] args) {
        SmsMessage sms = new SmsMessage();


        boolean sendOk = new QCloudSmsSender().send(sms);

        System.out.println(sendOk);
        System.out.println("===============finished!===================");
    }

}
