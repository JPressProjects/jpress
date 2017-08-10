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

public class TwitterConnector extends OauthConnector {

	public TwitterConnector(String name, String appkey, String appSecret) {
		super(name, appkey, appSecret);
	}

	@Override
	public String createAuthorizeUrl(String state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OauthUser getOauthUser(String code) {
		// TODO Auto-generated method stub
		return null;
	}
}
