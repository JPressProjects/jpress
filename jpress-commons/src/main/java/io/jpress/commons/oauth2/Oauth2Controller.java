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
package io.jpress.commons.oauth2;

import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;

public abstract class Oauth2Controller extends JbootController {

    // www.xxx.com/xxx/qq
    // www.xxx.com/xxx/weibo
    public void index() {

        String para = getPara();
        if (StrUtil.isBlank(para)) {
            renderError(404);
            return;
        }

        OauthConnector connector = onConnectorGet(para);

        // 不支持此 connectior
        if (connector == null) {
            renderError(404);
            return;
        }

        String state = StrUtil.uuid();
        String url = connector.getAuthorizeUrl(state);

        setSessionAttr("oauth_state", state);
        redirect(url);
    }

    // www.xxx.com/xxx/callback/qq
    // www.xxx.com/xxx/callback/weibo
    public void callback() {
        String sessionState = getSessionAttr("oauth_state");
        String state = getPara("state");

        if (!sessionState.equals(state)) {
            onAuthorizeError("state not validate");
            return;
        }

        String code = getPara("code");
        if (null == code || "".equals(code.trim())) {
            onAuthorizeError("can't get code");
            return;
        }

        String processerName = getPara();
        OauthConnector connector = onConnectorGet(processerName);

        OauthUser ouser = null;
        try {
            ouser = connector.getUser(code);
        } catch (Exception e) {
            onAuthorizeError("get oauth user exception:" + e.getMessage());
            return;
        }

        if (ouser == null) {
            onAuthorizeError("can't get user info!");
            return;
        }

        onAuthorizeSuccess(ouser);

    }

    public abstract void onAuthorizeSuccess(OauthUser oauthUser);

    public abstract void onAuthorizeError(String errorMessage);

    public abstract OauthConnector onConnectorGet(String para);

}
