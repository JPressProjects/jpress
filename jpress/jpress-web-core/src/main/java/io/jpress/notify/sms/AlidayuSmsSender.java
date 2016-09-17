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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.log.Log;

import io.jpress.core.Jpress;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.HttpUtils;
import io.jpress.utils.StringUtils;

public class AlidayuSmsSender implements ISmsSender {
	private static final Log log = Log.getLog(AlidayuSmsSender.class);

	/**
	 * http://open.taobao.com/doc2/apiDetail.htm?spm=a219a.7395905.0.0.Y1YXKM&
	 * apiId=25443
	 */

	@Override
	public boolean send(SmsMessage sms) {
		String app_key =  OptionQuery.me().findValue("sms_app_key");//"your app key";
		String app_secret =  OptionQuery.me().findValue("sms_app_secret");//"your app secret"

		String sendResult = doSend(sms, app_key, app_secret);

		if (Jpress.isDevMode()) {
			System.err.println("sms send result:" + sendResult);
		}

		if (StringUtils.isNotBlank(sendResult)) {
			if (sendResult != null && sendResult.contains("alibaba_aliqin_fc_sms_num_send_response")
					&& sendResult.contains("success") && sendResult.contains("true")) {
				return true;
			}
		}
		return false;
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

		String sign = EncryptUtils.signForRequest(params, app_secret);
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

	public static void main(String[] args) {
		SmsMessage sms = new SmsMessage();

		sms.setContent("test");
		sms.setRec_num("18600000000");
		sms.setTemplate("SMS_6730856");
		sms.setParam("{\"code\":\"8888\",\"product\":\"JPress\",\"customer\":\"杨福海\"}");
		sms.setSign_name("登录验证");

		boolean sendOk = new AlidayuSmsSender().send(sms);

		System.out.println(sendOk);
		System.out.println("===============finished!===================");
	}

}
