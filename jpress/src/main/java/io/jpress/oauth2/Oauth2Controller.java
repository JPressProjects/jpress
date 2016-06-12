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

import java.util.UUID;

import io.jpress.core.JBaseController;

public abstract class Oauth2Controller extends JBaseController {

	public void index() {
		String processerName = getPara();
		OauthConnector op = ProcesserFactory.createProcesser(processerName);
		String state = UUID.randomUUID().toString().replace("-", "");

		String requestUrl = getRequest().getRequestURL().toString();
		String callBackUrl = requestUrl.replace("/" + processerName, "/callback/" + processerName);
		String url = op.getAuthorizeUrl(state, callBackUrl);
		
		setSessionAttr("oauth_state", state);
		redirect(url);
	}

	// xxx/callback/qq
	// xxx/callback/weibo
	// xxx/callback/qq
	public void callback() {
		String sessionState = getSessionAttr("oauth_state");
		String state = getPara("state");

		if (!sessionState.equals(state)) {
			onError("state not validate");
			return;
		}

		String code = getPara("code");
		if (null == code || "".equals(code.trim())) {
			onError("can't get code");
			return;
		}

		String processerName = getPara();
		OauthConnector op = ProcesserFactory.createProcesser(processerName);
		OauthUser ouser = op.getUser(code);
		onCallBack(ouser);
	}

	public abstract void onCallBack(OauthUser oauthUser);

	public abstract void onError(String errorMessage);

}
