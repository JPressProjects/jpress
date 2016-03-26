package io.jpress.oauth2;

import io.jpress.utils.HttpUtils;

import com.jfinal.log.Log;

public abstract class OauthProcesser {

	// 第一步，构建跳转的URL，跳转后用户登录成功，返回到callback url，并带上code
	// 第二步，通过code，获取access token
	// 第三步，通过 access token 获取用户的open_id
	// 第四步，通过 open_id 获取用户信息
	private static final Log LOGGER = Log.getLog(OauthProcesser.class);
	
	
	private String clientId ;
	private String clientSecret ;
	private String name ;
	private String redirectUri ;
	
	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getClientSecret() {
		return clientSecret;
	}


	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getRedirectUri() {
		return redirectUri;
	}


	public String getAuthorizeUrl(String state,String redirectUri){
		this.redirectUri = redirectUri;
		return createAuthorizeUrl(state);
	}
	

	protected String httpGet(String url) {
		try {
			return HttpUtils.get(url);
		} catch (Exception e) {
			LOGGER.error("httpGet error", e);
		}
		return null;
	}

	public abstract String createAuthorizeUrl(String state);
	protected abstract OauthUser getOauthUser(String code);
	

	public OauthUser getUser(String code) {
		return getOauthUser(code);
	}

}
