package io.jpress.controller.front;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Option;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;


@UrlMapping(url = "/wechat")
public class WechatMessageController extends MsgControllerAdapter{

	
	@Override
	public void index() {
		
		
		
		super.index();
	}
	

	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public ApiConfig getApiConfig() {
		ApiConfig config = new ApiConfig();
		
		config.setAppId(Option.findValue("wechat_appid"));
		config.setAppSecret(Option.findValue("wechat_appsecret"));
		config.setToken(Option.findValue("wechat_token"));
		return config;
	}

	
}
