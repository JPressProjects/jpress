/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.notify.sms;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.log.Log;

import io.jpress.utils.HttpUtils;
import io.jpress.utils.StringUtils;

public class AlidayuSmsSender implements ISmsSender {
	private static final String CHARSET_UTF8 = "utf-8";
	private static final Log log = Log.getLog(AlidayuSmsSender.class);

	/**
	 * http://open.taobao.com/doc2/apiDetail.htm?spm=a219a.7395905.0.0.Y1YXKM&
	 * apiId=25443
	 */

	@Override
	public String send(SmsMessage sms) {
		String app_key = "your app key";
		String app_secret = "your app secret";
		return doSend(sms, app_key, app_secret);
	}

	private static String doSend(SmsMessage sms, String app_key, String app_secret) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("format", "json");
		params.put("method", "alibaba.aliqin.fc.sms.num.send");
		params.put("sign_method", "md5");

		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		params.put("timestamp", timestamp);
		params.put("v", "2.0");
		params.put("rec_num", sms.getRec_num());
		params.put("sms_free_sign_name", sms.getSign_name());
		params.put("sms_param", sms.getParam());
		params.put("sms_template_code", sms.getTemplate());
		params.put("sms_type", "normal");
		params.put("app_key", app_key);

		String sign = signTopRequest(params, app_secret);
		params.put("sign", sign);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		try {
			return HttpUtils.post("http://gw.api.taobao.com/router/rest", params, headers);
		} catch (Exception e) {
			log.error("AlidayuSmsSender doSend http exception", e);
		}
		return null;
	}

	public static String signTopRequest(Map<String, String> params, String secret) {
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		StringBuilder query = new StringBuilder();
		query.append(secret);
		for (String key : keys) {
			String value = params.get(key);
			if (StringUtils.areNotEmpty(key, value)) {
				query.append(key).append(value);
			}
		}

		query.append(secret);
		byte[] bytes = encryptMD5(query.toString());
		return byte2hex(bytes);
	}

	public static byte[] encryptMD5(String data) {
		try {
			return encryptMD5(data.getBytes(CHARSET_UTF8));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encryptMD5(byte[] data) throws IOException {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			bytes = md.digest(data);
		} catch (GeneralSecurityException gse) {
			throw new IOException(gse.toString());
		}
		return bytes;
	}

	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	public static void main(String[] args) {
		SmsMessage sms = new SmsMessage();
		
		sms.setContent("test");
		sms.setRec_num("186000000000");
		sms.setTemplate("SMS_6730856");
		sms.setParam("{\"code\":\"1234\",\"product\":\"JPress\",\"customer\":\"杨福海\"}");
		sms.setSign_name("登录验证");

		String retString = new AlidayuSmsSender().send(sms);

		System.out.println(retString);
		System.out.println("===============finished!===================");
	}

}
