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
package io.jpress.oauth2.connector;

import io.jpress.oauth2.OauthConnector;
import io.jpress.oauth2.OauthUser;

public class WeiboConnector extends OauthConnector {

	public WeiboConnector() {
		setClientId("4067662683");
		setClientSecret("02f2d6c8b5993acd3e16c3b8f24035c3");
		setName("weibo");
	}

	public String createAuthorizeUrl(String state) {

		StringBuilder urlBuilder = new StringBuilder(
				"https://api.weibo.com/oauth2/authorize?");
		urlBuilder.append("response_type=code");
		urlBuilder.append("&client_id=" + getClientId());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&state=" + state);

		return urlBuilder.toString();
	}

	protected String getAccessToken(String code) {

		StringBuilder urlBuilder = new StringBuilder(
				"https://api.weibo.com/oauth2/access_token?");
		urlBuilder.append("grant_type=authorization_code");
		urlBuilder.append("&client_id=" + getClientId());
		urlBuilder.append("&client_secret=" + getClientSecret());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = httpGet(url);

		return null;
	}

	public String getOpenId(String accessToken) {

		String url = "https://api.weibo.com/oauth2/get_token_info?"
				+ "access_token=" + accessToken;

		String httpString = httpGet(url);

		return null;
	}
	

	protected OauthUser getOauthUser(String code) {
		String accessToken = getAccessToken(code);
		String openId = getAccessToken(accessToken);
		
		
		String url = "https://api.weibo.com/2/users/show.json?"
				+ "access_token=" + accessToken + "&uid=" + openId;

		String httpString = httpGet(url);

		OauthUser user = new OauthUser();
		user.setSource(getName());
		return user;
	}

}
