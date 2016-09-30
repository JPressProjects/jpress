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
import io.jpress.utils.StringUtils;

public class OSChinaConnector extends OauthConnector {

	public OSChinaConnector(String name, String appkey, String appSecret) {
		super(name, appkey, appSecret);
	}

	// DOC : http://www.oschina.net/openapi/

	// public OSChinaConnector() {
	// setClientId(OptionQuery.findValue("oauth2_oschina_appkey"));
	// setClientSecret(OptionQuery.findValue("oauth2_oschina_appsecret"));
	// setName("oschina");
	// }

	/**
	 * doc: http://www.oschina.net/openapi/docs/oauth2_authorize
	 */
	public String createAuthorizeUrl(String state) {

		StringBuilder urlBuilder = new StringBuilder("http://www.oschina.net/action/oauth2/authorize?");
		urlBuilder.append("response_type=code");
		urlBuilder.append("&client_id=" + getClientId());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&state=" + state);

		return urlBuilder.toString();
	}

	protected String getAccessToken(String code) {

		StringBuilder urlBuilder = new StringBuilder("http://www.oschina.net/action/openapi/token?");
		urlBuilder.append("grant_type=authorization_code");
		urlBuilder.append("&dataType=json");
		urlBuilder.append("&client_id=" + getClientId());
		urlBuilder.append("&client_secret=" + getClientSecret());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = httpGet(url);
		// {"access_token":"07a2aeb2-0790-4a36-ae24-40b90c4fcfc1",
		// "refresh_token":"bfd67382-f740-4735-b7f0-86fb9a2e49dc",
		// "uid":111634,
		// "token_type":"bearer",
		// "expires_in":604799}

		if (StringUtils.isBlank(httpString)) {
			return null;
		}

		JSONObject json = JSONObject.parseObject(httpString);
		return json.getString("access_token");
	}

	@Override
	protected OauthUser getOauthUser(String code) {

		String accessToken = getAccessToken(code);

		String url = "http://www.oschina.net/action/openapi/user?access_token=" + accessToken + "&dataType=json";

		String httpString = httpGet(url);
		// {"gender":"male","name":"michaely","location":"北京 朝阳","id":111634,
		// "avatar":"http://static.oschina.net/uploads/user/55/111634_50.jpg?t=1414374101000",
		// "email":"fuhai999@gmail.com","url":"http://my.oschina.net/yangfuhai"}

		JSONObject json = JSONObject.parseObject(httpString);

		OauthUser user = new OauthUser();
		user.setAvatar(json.getString("avatar"));
		user.setOpenId(json.getString("id"));
		user.setNickname(json.getString("name"));
		user.setGender(json.getString("gender"));
		user.setSource(getName());

		return user;
	}

}
