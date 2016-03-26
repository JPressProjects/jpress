package io.jpress.oauth2.processer;

import io.jpress.oauth2.OauthProcesser;
import io.jpress.oauth2.OauthUser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class QQProcesser extends OauthProcesser {


	/**
	 * http://wiki.connect.qq.com/%E4%BD%BF%E7%94%A8authorization_code
	 */
	public QQProcesser() {

		setClientId("101296865");
		setClientSecret("9c8ed6da1cf7f26dacf02587db74f2ea");
		setName("qq");
	}

	@Override
	public String createAuthorizeUrl(String state) {
		
		StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/authorize?");
		sb.append("response_type=code");
		sb.append("&client_id="+getClientId());
		sb.append("&redirect_uri="+getRedirectUri());
		sb.append("&state="+state);
		
		return sb.toString();
	}
	

	protected String getAccessToken(String code) {
		
		StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/token?");
		sb.append("grant_type=authorization_code");
		sb.append("&code="+code);
		sb.append("&client_id="+getClientId());
		sb.append("&client_secret="+getClientSecret());
		sb.append("&redirect_uri="+getRedirectUri());

		String httpString = httpGet(sb.toString());
		// access_token=2D6FE76*****24AB&expires_in=7776000&refresh_token=7CD56****218

		return httpString.substring(httpString.indexOf("=")+1,httpString.indexOf("&"));
	}

	protected String getOpenId(String accessToken,String code) {

		StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/me?");
		sb.append("access_token="+accessToken);
		
		String httpString = httpGet(sb.toString());
		// callback(
		// {"client_id":"10***65","openid":"F8D32108D*****D"}
		// );

		return httpString.substring(httpString.indexOf(":")+2,httpString.indexOf(",")-1);
	}

	@Override
	protected OauthUser getOauthUser(String code) {
		String accessToken = getAccessToken(code);
		String openId = getOpenId(accessToken, code);
		
		StringBuilder sb = new StringBuilder("https://graph.qq.com/user/get_user_info?");
		sb.append("access_token="+accessToken);
		sb.append("&oauth_consumer_key="+getClientId());
		sb.append("&openid="+openId);
		sb.append("&format=format");

		String httpString = httpGet(sb.toString());

		JSONObject json = JSON.parseObject(httpString);
		OauthUser user = new OauthUser();

		user.setAvatar(json.getString("figureurl_2"));
		user.setNickname(json.getString("nickname"));
		user.setOpenId(json.getString("openid"));
		user.setSource(getName());

		return user;
	}

}
