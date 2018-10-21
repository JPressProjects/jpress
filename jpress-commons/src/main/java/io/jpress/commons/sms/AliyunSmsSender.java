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
 * 阿里云短信发送
 * api 接口文档 ：https://help.aliyun.com/document_detail/56189.html?spm=a2c4g.11186623.6.590.263891ebLwA3nl
 */
public class AliyunSmsSender implements ISmsSender {
    private static final Log log = Log.getLog(AliyunSmsSender.class);


    @Override
    public boolean send(SmsMessage sms) {

        String app_key = "your app key";
        String app_secret = "your app secret";

        return false;
    }


    public static void main(String[] args) {
        SmsMessage sms = new SmsMessage();


        boolean sendOk = new AliyunSmsSender().send(sms);

        System.out.println(sendOk);
        System.out.println("===============finished!===================");
    }

}
