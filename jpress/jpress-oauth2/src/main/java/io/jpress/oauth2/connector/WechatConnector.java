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

public class WechatConnector extends OauthConnector {

	public WechatConnector(String name, String appkey, String appSecret) {
		super(name, appkey, appSecret);
	}

	// DOC
	// https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&id=open1419316505

	// public WechatConnector() {
	// setClientId(OptionQuery.findValue("oauth2_wechat_appkey"));
	// setClientSecret(OptionQuery.findValue("oauth2_wechat_appsecret"));
	// setName("wechat");
	// }

	public String createAuthorizeUrl(String state) {

		// https://open.weixin.qq.com/connect/qrconnect?
		// appid=APPID
		// &redirect_uri=REDIRECT_URI
		// &response_type=code
		// &scope=SCOPE
		// &state=STATE#wechat_redirect

		StringBuilder urlBuilder = new StringBuilder("https://open.weixin.qq.com/connect/qrconnect?");
		urlBuilder.append("response_type=code");
		urlBuilder.append("&scope=snsapi_login");
		urlBuilder.append("&appid=" + getClientId());
		urlBuilder.append("&redirect_uri=" + getRedirectUri());
		urlBuilder.append("&state=" + state);
		urlBuilder.append("#wechat_redirect");

		return urlBuilder.toString();
	}

	protected JSONObject getAccessToken(String code) {

		// https://api.weixin.qq.com/sns/oauth2/access_token?
		// appid=APPID
		// &secret=SECRET
		// &code=CODE
		// &grant_type=authorization_code

		StringBuilder urlBuilder = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?");
		urlBuilder.append("grant_type=authorization_code");
		urlBuilder.append("&appid=" + getClientId());
		urlBuilder.append("&secret=" + getClientSecret());
		urlBuilder.append("&code=" + code);

		String url = urlBuilder.toString();

		String httpString = httpGet(url);

		/**
		 * { "access_token":"ACCESS_TOKEN", "expires_in":7200,
		 * "refresh_token":"REFRESH_TOKEN", "openid":"OPENID", "scope":"SCOPE",
		 * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL" }
		 */

		return JSONObject.parseObject(httpString);
	}

	@Override
	protected OauthUser getOauthUser(String code) {

		JSONObject tokenJson = getAccessToken(code);
		String accessToken = tokenJson.getString("access_token");
		String openId = tokenJson.getString("openid");

		// https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID

		String url = "https://api.weixin.qq.com/sns/userinfo?" + "access_token=" + accessToken + "&openid=" + openId;

		String httpString = httpGet(url);

		/**
		 * { "openid":"OPENID", "nickname":"NICKNAME", "sex":1,
		 * "province":"PROVINCE", "city":"CITY", "country":"COUNTRY",
		 * "headimgurl":
		 * "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
		 * "privilege":[ "PRIVILEGE1", "PRIVILEGE2" ], "unionid":
		 * " o6_bmasdasdsad6_2sgVt7hMZOPfL" }
		 */

		OauthUser user = new OauthUser();
		JSONObject json = JSONObject.parseObject(httpString);
		user.setAvatar(json.getString("headimgurl"));
		user.setNickname(json.getString("nickname"));
		user.setOpenId(openId);
		int sex = json.getIntValue("sex");
		user.setGender(sex == 1 ? "male" : "female");

		user.setSource(getName());

		return user;
	}

}
