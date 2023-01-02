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

import com.jfinal.kit.Base64Kit;
import com.jfinal.log.Log;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * 阿里云短信发送
 * api 接口文档 ：https://help.aliyun.com/document_detail/56189.html?spm=a2c4g.11186623.6.590.263891ebLwA3nl
 */
public class AliyunSmsSender implements SmsSender {

    private static final Log log = Log.getLog(AliyunSmsSender.class);

    @Override
    public boolean send(SmsMessage sms) {

        String app_key = JPressOptions.get(JPressConsts.OPTION_CONNECTION_SMS_APPID);
        String app_secret = JPressOptions.get(JPressConsts.OPTION_CONNECTION_SMS_APPSECRET);


        Map<String, String> params = new HashMap<>();
        params.put("AccessKeyId", app_key);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String timestamp = df.format(new Date());
        params.put("Timestamp", timestamp);
        params.put("Format", "JSON");
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", StrUtil.uuid());


        params.put("Action", "SendSms");
        params.put("Version", "2017-05-25");
        params.put("RegionId", "cn-hangzhou");
        params.put("PhoneNumbers", sms.getMobile());
        params.put("SignName", sms.getSign());
        params.put("TemplateCode", sms.getTemplate());

        if (StrUtil.isNotBlank(sms.getCode())) {
            params.put("TemplateParam", "{\"code\":" + sms.getCode() + "}");
        }

        try {
            String url = doSignAndGetUrl(params, app_secret);
            String content = HttpUtil.httpGet(url);
            if (StrUtil.isNotBlank(content) && content.contains("\"Code\":\"OK\"")) {
                return true;
            } else {
                log.error("aliyun sms send error : " + content);
            }
        } catch (Exception e) {
            log.error("AliyunSmsSender exception", e);
        }

        return false;
    }

    public static String doSignAndGetUrl(Map<String, String> params, String secret) throws Exception {

        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<>();
        sortParas.putAll(params);


        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(params.get(key).toString()));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(secret + "&", stringToSign.toString());


        String signature = specialUrlEncode(sign);
        return "http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortQueryStringTmp;
    }

    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return Base64Kit.encode(signData);
//        return new sun.misc.BASE64Encoder().encode(signData);
    }


    public static void main(String[] args) {

        String app_key = "";
        String app_secret = "";

        JPressOptions.set(JPressConsts.OPTION_CONNECTION_SMS_APPID, app_key);
        JPressOptions.set(JPressConsts.OPTION_CONNECTION_SMS_APPSECRET, app_secret);

        SmsMessage sms = new SmsMessage();
        sms.setMobile("18611220000");
        sms.setTemplate("SMS_148593333");
        sms.setSign("JPress");
        sms.setCode("1234");

        boolean sendOk = new AliyunSmsSender().send(sms);

        System.out.println(sendOk);
        System.out.println("===============finished!===================");
    }

}
