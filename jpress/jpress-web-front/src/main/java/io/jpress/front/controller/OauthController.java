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
package io.jpress.front.controller;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jpress.Consts;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.Metadata;
import io.jpress.model.User;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.oauth2.Oauth2Controller;
import io.jpress.oauth2.OauthConnector;
import io.jpress.oauth2.OauthUser;
import io.jpress.oauth2.connector.FacebookConnector;
import io.jpress.oauth2.connector.GithubConnector;
import io.jpress.oauth2.connector.LinkedinConnector;
import io.jpress.oauth2.connector.OSChinaConnector;
import io.jpress.oauth2.connector.QQConnector;
import io.jpress.oauth2.connector.TwitterConnector;
import io.jpress.oauth2.connector.WechatConnector;
import io.jpress.oauth2.connector.WeiboConnector;
import io.jpress.router.RouterMapping;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/oauth")
public class OauthController extends Oauth2Controller {

	@Override
	public void index() {
		String gotoUrl = getPara("goto");
		if (StringUtils.isNotBlank(gotoUrl)) {
			setSessionAttr("_goto_url", gotoUrl);
		}
		super.index();
	}

	@Override
	public void onAuthorizeSuccess(OauthUser ouser) {
		User user = UserQuery.me().findFirstFromMetadata(ouser.getSource() + "_open_id", ouser.getOpenId());
		if (null == user) { // first login
			user = new User();
			user.setAvatar(ouser.getAvatar());
			user.setNickname(ouser.getNickname());
			user.setCreateSource(ouser.getSource());
			user.setCreated(new Date());
			user.setGender(ouser.getGender());
			user.setSalt(EncryptUtils.salt());

			user.save();

			BigInteger userId = user.getId();
			if (userId != null && userId.compareTo(BigInteger.ZERO) > 0) {
				Metadata md = user.createMetadata();
				md.setMetaKey(ouser.getSource() + "_open_id");
				md.setMetaValue(ouser.getOpenId());
				md.saveOrUpdate();
			}
		}

		doAuthorizeSuccess(user);
	}

	@Override
	public void onAuthorizeError(String errorMessage) {
		String redirect = getPara("goto");
		redirect = StringUtils.isNotBlank(redirect) ? redirect : Consts.ROUTER_USER_LOGIN;
		redirect(redirect);
	}

	private void doAuthorizeSuccess(User user) {
		CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
		MessageKit.sendMessage(Actions.USER_LOGINED, user);

		String redirect = getPara("goto");
		if (StringUtils.isBlank(redirect)) {
			redirect = getSessionAttr("_goto_url");
		}

		if (StringUtils.isNotBlank(redirect)) {
			redirect = StringUtils.urlRedirect(redirect);
		} else {
			redirect = Consts.ROUTER_USER_CENTER;
		}

		redirect(redirect);
	}

	@Override
	public OauthConnector onConnectorGet(String processerName) {
		String appkey = OptionQuery.me().findValue("oauth2_" + processerName + "_appkey");
		String appsecret = OptionQuery.me().findValue("oauth2_" + processerName + "_appsecret");

		return getConnector(processerName, appkey, appsecret);
	}

	private static final Map<String, OauthConnector> connectorMap = new HashMap<String, OauthConnector>();

	private static OauthConnector getConnector(String name, String appkey, String appSecret) {
		OauthConnector connector = connectorMap.get(name);
		if (connector == null) {

			if ("qq".equals(name)) {
				connector = new QQConnector(name, appkey, appSecret);
			} else if ("wechat".equals(name)) {
				connector = new WechatConnector(name, appkey, appSecret);
			} else if ("weibo".equals(name)) {
				connector = new WeiboConnector(name, appkey, appSecret);
			} else if ("oschina".equals(name)) {
				connector = new OSChinaConnector(name, appkey, appSecret);
			} else if ("github".equals(name)) {
				connector = new GithubConnector(name, appkey, appSecret);
			} else if ("facebook".equals(name)) {
				connector = new FacebookConnector(name, appkey, appSecret);
			} else if ("twitter".equals(name)) {
				connector = new TwitterConnector(name, appkey, appSecret);
			} else if ("linkedin".equals(name)) {
				connector = new LinkedinConnector(name, appkey, appSecret);
			}

			if (connector != null) {
				connectorMap.put(name, connector);
			}
		}

		return connector;
	}

}
