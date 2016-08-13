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

import com.alibaba.fastjson.JSONObject;

import io.jpress.oauth2.OauthConnector;
import io.jpress.oauth2.OauthUser;

public class GithubConnector extends OauthConnector {

	public GithubConnector(String name, String appkey, String appSecret) {
		super(name, appkey, appSecret);
	}

	@Override
	public String createAuthorizeUrl(String state) {
		StringBuilder sb = new StringBuilder("https://github.com/login/oauth/authorize?");
		sb.append("scope=user");
		sb.append("&client_id=" + getClientId());
		sb.append("&redirect_uri=" + getRedirectUri());
		sb.append("&state=" + state);

		return sb.toString();
	}

	@Override
	protected OauthUser getOauthUser(String code) {
		String accessToken = getAccessToken(code);

		String url = "https://api.github.com/user?access_token=" + accessToken;

		String httpString = httpGet(url);
		JSONObject json = JSONObject.parseObject(httpString);

		OauthUser user = new OauthUser();
		user.setAvatar(json.getString("avatar_url"));
		user.setOpenId(json.getString("id"));
		user.setNickname(json.getString("login"));
		user.setSource(getName());

		return null;
	}

	protected String getAccessToken(String code) {

		StringBuilder urlBuilder = new StringBuilder("https://github.com/login/oauth/access_token?");
		urlBuilder.append("client_id=" + getClientId());
		urlBuilder.append("&client_secret=" + getClientSecret());
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = httpGet(url);
		JSONObject json = JSONObject.parseObject(httpString);
		return json.getString("access_token");
	}
}
