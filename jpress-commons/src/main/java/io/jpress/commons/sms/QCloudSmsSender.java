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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import java.util.Random;

/**
 * 腾讯云短信发送
 * api 接口文档 ：https://cloud.tencent.com/document/product/382/5976
 */
public class QCloudSmsSender implements SmsSender {

    private static final String SMS_JSON = "{\"ext\":\"\",\"extend\":\"\",\"params\":[\"{code}\"],\"sig\":\"{sig}\",\"sign\":\"{sign}\",\"tel\":{\"mobile\":\"{mobile}\",\"nationcode\":\"86\"},\"time\":{time},\"tpl_id\":{tpl_id}}";
    private static final String SMS_NO_CODE_JSON = "{\"ext\":\"\",\"extend\":\"\",\"params\":[],\"sig\":\"{sig}\",\"sign\":\"{sign}\",\"tel\":{\"mobile\":\"{mobile}\",\"nationcode\":\"86\"},\"time\":{time},\"tpl_id\":{tpl_id}}";


    @Override
    public boolean send(SmsMessage sms) {

        String app_key = JPressOptions.get(JPressConsts.OPTION_CONNECTION_SMS_APPID);
        String app_secret = JPressOptions.get(JPressConsts.OPTION_CONNECTION_SMS_APPSECRET);

        String random = new Random().nextInt(1000000) + "";
        String time = System.currentTimeMillis() / 1000 + "";

        String srcStr = "appkey=" + app_secret + "&random=" + random + "&time=" + time + "&mobile=" + sms.getMobile();
        String sig = HashKit.sha256(srcStr);

        boolean hasCode = StrUtil.isNotBlank(sms.getCode());

        String postContent = (hasCode ? SMS_JSON.replace("{code}", sms.getCode()) : SMS_NO_CODE_JSON)
                .replace("{sig}", sig)
                .replace("{sign}", sms.getSign())
                .replace("{mobile}", sms.getMobile())
                .replace("{time}", time)
                .replace("{tpl_id}", sms.getTemplate());

        String url = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + app_key + "&random=" + random;

        String content = HttpUtil.httpPost(url, postContent);

        if (StrUtil.isBlank(content)) {
            return false;
        }

        JSONObject resultJson = JSON.parseObject(content);
        Integer result = resultJson.getInteger("result");
        if (result != null && result == 0) {
            return true;
        } else {
            LogKit.error("qcloud sms send error : " + content);
            return false;
        }
    }


    public static void main(String[] args) {

        String app_id = "";
        String app_key = "";

        JPressOptions.set(JPressConsts.OPTION_CONNECTION_SMS_APPID, app_id);
        JPressOptions.set(JPressConsts.OPTION_CONNECTION_SMS_APPSECRET, app_key);


        SmsMessage sms = new SmsMessage();
        sms.setMobile("18611220000");
        sms.setTemplate("215659");
        sms.setSign("JPress大本营");
        sms.setCode("1234");

        boolean sendOk = new QCloudSmsSender().send(sms);

        System.out.println(sendOk);
        System.out.println("===============finished!===================");
    }

}
