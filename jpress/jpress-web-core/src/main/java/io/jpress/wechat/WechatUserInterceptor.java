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
package io.jpress.wechat;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import io.jpress.Consts;
import io.jpress.model.query.OptionQuery;
import io.jpress.utils.StringUtils;

public class WechatUserInterceptor implements Interceptor {

	public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid={appid}"
			+ "&redirect_uri={redirecturi}" + "&response_type=code" + "&scope=snsapi_userinfo"
			+ "&state=235#wechat_redirect";

	@Override
	public void intercept(Invocation inv) {

		Controller controller = inv.getController();

		String userJson = inv.getController().getSessionAttr(Consts.SESSION_WECHAT_USER);

		if (StringUtils.isBlank(userJson)) {
			inv.invoke();
			return;
		}

		String appid = OptionQuery.me().findValue("wechat_appid");
		if (StringUtils.isBlank(appid)) {
			inv.invoke();
			return;
		}

		HttpServletRequest request = controller.getRequest();
		// 获取用户将要去的路径
		String queryString = request.getQueryString();

		// 被拦截前的请求URL
		String toUrl = request.getRequestURI();
		if (StringUtils.isNotBlank(queryString)) {
			toUrl = toUrl.concat("?").concat(queryString);
		}
		toUrl = StringUtils.urlEncode(toUrl);

		String redirectUrl = request.getScheme() + "://" + request.getServerName() + "/wechat/callback?goto=" + toUrl;
		redirectUrl = StringUtils.urlEncode(redirectUrl);

		String url = AUTHORIZE_URL.replace("{redirecturi}", redirectUrl).replace("{appid}", appid.trim());
		controller.redirect(url);

	}

}
