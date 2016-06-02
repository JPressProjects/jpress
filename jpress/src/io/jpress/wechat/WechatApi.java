package io.jpress.wechat;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.api.UserApi;

import io.jpress.model.Option;
import io.jpress.utils.HttpUtils;

public class WechatApi {

	public static ApiConfig getApiConfig() {
		ApiConfig config = new ApiConfig();
		config.setAppId(Option.findValue("wechat_appid"));
		config.setAppSecret(Option.findValue("wechat_appsecret"));
		config.setToken(Option.findValue("wechat_token"));
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
