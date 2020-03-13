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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jboot.utils.StrUtil;
import io.jpress.commons.oauth2.OauthConnector;
import io.jpress.commons.oauth2.OauthUser;

public class QQConnector extends OauthConnector {

    public QQConnector(String appkey, String appSecret) {
        super("qq", appkey, appSecret);
    }

    /**
     * http://wiki.connect.qq.com/%E4%BD%BF%E7%94%A8authorization_code
     */
    @Override
    public String createAuthorizeUrl(String state) {

        StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/authorize?");
        sb.append("response_type=code");
        sb.append("&client_id=" + getClientId());
        sb.append("&redirect_uri=" + getRedirectUri());
        sb.append("&state=" + state);

        return sb.toString();
    }

    protected String getAccessToken(String code) {

        StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/token?");
        sb.append("grant_type=authorization_code");
        sb.append("&code=" + code);
        sb.append("&client_id=" + getClientId());
        sb.append("&client_secret=" + getClientSecret());
        sb.append("&redirect_uri=" + getRedirectUri());

        String httpString = httpGet(sb.toString());

        //callback( {"error":100010,"error_description":"redirect uri is illegal"} );
        //access_token=2D6FE76*****24AB&expires_in=7776000&refresh_token=7CD56****218

        if (StrUtil.isBlank(httpString)) {
            return null;
        }

        if (httpString.contains("\"error\":")) {
            throw new IllegalStateException(httpString);
        }

        return httpString.substring(httpString.indexOf("=") + 1, httpString.indexOf("&"));
    }

    protected String getOpenId(String accessToken, String code) {

        StringBuilder sb = new StringBuilder("https://graph.qq.com/oauth2.0/me?");
        sb.append("access_token=" + accessToken);

        String httpString = httpGet(sb.toString());
        // callback(
        // {"client_id":"10***65","openid":"F8D32108D*****D"}
        // );

        if (StrUtil.isBlank(httpString)) {
            return null;
        }

        if (httpString.contains("\"error\":")) {
            throw new IllegalStateException(httpString);
        }

        return httpString.substring(httpString.lastIndexOf(":") + 2, httpString.lastIndexOf("\""));
    }

    @Override
    protected OauthUser getOauthUser(String code) {
        String accessToken = getAccessToken(code);
        if (StrUtil.isBlank(accessToken)) {
            return null;
        }
        String openId = getOpenId(accessToken, code);
        if (StrUtil.isBlank(openId)) {
            return null;
        }

        StringBuilder sb = new StringBuilder("https://graph.qq.com/user/get_user_info?");
        sb.append("access_token=" + accessToken);
        sb.append("&oauth_consumer_key=" + getClientId());
        sb.append("&openid=" + openId);
        sb.append("&format=format");

        String httpString = httpGet(sb.toString());

        if (StrUtil.isBlank(httpString)) {
            return null;
        }

        JSONObject json = JSON.parseObject(httpString);
        String gender = json.getString("gender");
        OauthUser user = new OauthUser();

        user.setAvatar(json.getString("figureurl_2"));
        user.setNickname(json.getString("nickname"));
        user.setOpenId(openId);
        user.setSource(getName());
        user.setGender("男".equals(gender) ? OauthUser.GENDER_MALE : ("女".equals(gender) ? OauthUser.GENDER_FEMALE : OauthUser.GENDER_UNKNOW));

        return user;
    }

}
