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

import io.jpress.oauth2.connector.OSChinaConnector;
import io.jpress.oauth2.connector.QQConnector;
import io.jpress.oauth2.connector.WechatConnector;
import io.jpress.oauth2.connector.WeiboConnector;

public class ProcesserFactory {

	private static final Map<String, OauthConnector> oauths = new HashMap<String, OauthConnector>();
	static {
		oauths.put("qq", new QQConnector());
		oauths.put("oschina", new OSChinaConnector());
		oauths.put("wechat", new WechatConnector());
		oauths.put("weibo", new WeiboConnector());
	}

	public static OauthConnector createProcesser(String key) {
		return oauths.get(key);
	}

}
