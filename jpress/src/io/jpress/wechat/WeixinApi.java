package io.jpress.wechat;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;

import io.jpress.model.Option;

public class WeixinApi {

	public static ApiConfig getApiConfig() {
		ApiConfig config = new ApiConfig();
		config.setAppId(Option.findValue("wechat_appid"));
		config.setAppSecret(Option.findValue("wechat_appsecret"));
		config.setToken(Option.findValue("wechat_token"));
		return config;
	}

	public static ApiResult createMenu(String jsonString) {
		ApiConfig ac = getApiConfig();
		ApiConfigKit.setThreadLocalApiConfig(ac);
		return MenuApi.createMenu(jsonString);
	}

}
