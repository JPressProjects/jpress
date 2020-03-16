/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.commons.oauth2.connector;

import com.alibaba.fastjson.JSONObject;
import io.jpress.commons.oauth2.OauthConnector;
import io.jpress.commons.oauth2.OauthUser;

import java.util.HashMap;
import java.util.Map;

public class DingdingConnector extends OauthConnector {

    public DingdingConnector(String appkey, String appSecret) {
        super("dingding", appkey, appSecret);
    }

    @Override
    public String createAuthorizeUrl(String state) {
        //https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=APPID&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=REDIRECT_URI
        StringBuilder sb = new StringBuilder("https://oapi.dingtalk.com/connect/oauth2/sns_authorize?");
        sb.append("scope=user");
        sb.append("&appid=" + getClientId());
        sb.append("&redirect_uri=" + getRedirectUri());
        sb.append("&response_type=code");
        sb.append("&state=" + state);

        return sb.toString();
    }

    @Override
    protected OauthUser getOauthUser(String code) {
        String accessToken = getAccessToken(code);

        JSONObject result = getPersistentCode(code, accessToken);

        String snsToken = getSnsToken(accessToken, result.getString("openid"), result.getString("persistent_code"));

        //https://oapi.dingtalk.com/sns/getuserinfo?sns_token=SNS_TOKEN
        String url = "https://oapi.dingtalk.com/sns/getuserinfo?sns_token=" + snsToken;

        String httpString = httpGet(url);
        JSONObject json = JSONObject.parseObject(httpString);
        JSONObject userInfo = json.getJSONObject("user_info");
        OauthUser user = new OauthUser();
        user.setOpenId(userInfo.getString("openid"));
        user.setNickname(userInfo.getString("nick"));
        user.setSource(getName());

        return user;
    }

    protected String getAccessToken(String code) {

        StringBuilder urlBuilder = new StringBuilder("https://oapi.dingtalk.com/sns/gettoken?");
        urlBuilder.append("appid=" + getClientId());
        urlBuilder.append("&appsecret=" + getClientSecret());

        String url = urlBuilder.toString();

        String httpString = httpGet(url);
        JSONObject json = JSONObject.parseObject(httpString);
        return json.getString("access_token");
    }

    protected JSONObject getPersistentCode(String code, String access_token) {
        //https://oapi.dingtalk.com/sns/get_persistent_code?access_token=ACCESS_TOKEN
        StringBuilder urlBuilder = new StringBuilder("https://oapi.dingtalk.com/sns/get_persistent_code?");
        urlBuilder.append("access_token=" + access_token);
        String url = urlBuilder.toString();

        Map<String, Object> param = new HashMap<>();
        param.put("tmp_auth_code", code);

        String httpString = httpPost(url, param);
        JSONObject json = JSONObject.parseObject(httpString);
        return json;
    }

    protected String getSnsToken(String access_token, String openId, String persistentCode) {
        //https://oapi.dingtalk.com/sns/get_sns_token?access_token=ACCESS_TOKEN
        StringBuilder urlBuilder = new StringBuilder("https://oapi.dingtalk.com/sns/get_sns_token?");
        urlBuilder.append("access_token=" + access_token);
        String url = urlBuilder.toString();

        Map<String, Object> param = new HashMap<>();
        param.put("openid", openId);
        param.put("persistent_code", persistentCode);

        String httpString = httpPost(url, param);
        JSONObject json = JSONObject.parseObject(httpString);
        return json.getString("sns_token");
    }
}
