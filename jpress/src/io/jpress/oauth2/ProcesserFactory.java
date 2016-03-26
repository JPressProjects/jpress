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

import io.jpress.oauth2.processer.OSChinaProcesser;
import io.jpress.oauth2.processer.QQProcesser;
import io.jpress.oauth2.processer.WechatProcesser;
import io.jpress.oauth2.processer.WeiboProcesser;

import java.util.HashMap;
import java.util.Map;

public class ProcesserFactory {

	private static final Map<String, OauthProcesser> oauths = new HashMap<String, OauthProcesser>();
	static {
		oauths.put("qq", new QQProcesser());
		oauths.put("oschina", new OSChinaProcesser());
		oauths.put("wechat", new WechatProcesser());
		oauths.put("weibo", new WeiboProcesser());
	}

	public static OauthProcesser createProcesser(String key) {
		return oauths.get(key);
	}

}
