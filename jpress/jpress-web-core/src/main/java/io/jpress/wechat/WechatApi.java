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
package io.jpress.wechat;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.api.UserApi;

import io.jpress.model.query.OptionQuery;
import io.jpress.utils.HttpUtils;

public class WechatApi {

	public static ApiConfig getApiConfig() {
		ApiConfig config = new ApiConfig();
		config.setAppId(OptionQuery.me().findValue("wechat_appid"));
		config.setAppSecret(OptionQuery.me().findValue("wechat_appsecret"));
		config.setToken(OptionQuery.me().findValue("wechat_token"));
		return config;
	}


	public static ApiResult createMenu(String jsonString) {
		return MenuApi.createMenu(jsonString);
	}
	
	public static ApiResult getUserInfo(String openId){
		return  UserApi.getUserInfo(openId);
	}
	
	public static ApiResult getOpenId(String appId, String appSecret, String code) {

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid={appid}"
				+ "&secret={secret}" + "&code={code}" + "&grant_type=authorization_code";

		String getOpenIdUrl = url.replace("{appid}", appId).replace("{secret}", appSecret)
				.replace("{code}", code);

		String jsonResult = null;
		try {
			jsonResult = HttpUtils.get(getOpenIdUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (jsonResult == null)
			return null;

		return new ApiResult(jsonResult);
	}

}
