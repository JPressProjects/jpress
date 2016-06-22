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

import java.util.HashMap;
import java.util.Map;

import io.jpress.oauth2.connector.FacebookConnector;
import io.jpress.oauth2.connector.GithubConnector;
import io.jpress.oauth2.connector.LinkedinConnector;
import io.jpress.oauth2.connector.OSChinaConnector;
import io.jpress.oauth2.connector.QQConnector;
import io.jpress.oauth2.connector.TwitterConnector;
import io.jpress.oauth2.connector.WechatConnector;
import io.jpress.oauth2.connector.WeiboConnector;

public class ProcesserFactory {

	private static final Map<String, OauthConnector> oauths = new HashMap<String, OauthConnector>();

	
	public static OauthConnector createProcesser(String name, String appkey, String appSecret) {
		OauthConnector connector = oauths.get(name);
		if (connector == null) {
			
			if ("qq".equals(name)) {
				connector = new QQConnector(appkey, appSecret);
			} else if ("wechat".equals(name)) {
				connector = new WechatConnector(appkey, appSecret);
			} else if ("weibo".equals(name)) {
				connector = new WeiboConnector(appkey, appSecret);
			} else if ("oschina".equals(name)) {
				connector = new OSChinaConnector(appkey, appSecret);
			} else if ("github".equals(name)) {
				connector = new GithubConnector(appkey, appSecret);
			} else if ("facebook".equals(name)) {
				connector = new FacebookConnector(appkey, appSecret);
			} else if ("twitter".equals(name)) {
				connector = new TwitterConnector(appkey, appSecret);
			} else if ("linkedin".equals(name)) {
				connector = new LinkedinConnector(appkey, appSecret);
			}

			if (connector != null) {
				oauths.put(name, connector);
			}
		}

		return connector;
	}

}
