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
package io.jpress.oauth2;

import java.util.Map;

import com.jfinal.log.Log;

import io.jpress.utils.HttpUtils;

public abstract class OauthConnector {

	// 第一步，构建跳转的URL，跳转后用户登录成功，返回到callback url，并带上code
	// 第二步，通过code，获取access token
	// 第三步，通过 access token 获取用户的open_id
	// 第四步，通过 open_id 获取用户信息
	private static final Log LOGGER = Log.getLog(OauthConnector.class);

	private String clientId;
	private String clientSecret;
	private String name;
	private String redirectUri;

	public OauthConnector(String name, String appkey, String appSecret) {
		this.clientId = appkey;
		this.clientSecret = appSecret;
		this.name = name;
	}

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

	public String getAuthorizeUrl(String state, String redirectUri) {
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
	protected String httpPost(String url,Map<String, ? extends Object> params) {
		try {
			return HttpUtils.post(url,params);
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
