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
package io.jpress.controller.front;

import io.jpress.Consts;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Metadata;
import io.jpress.model.User;
import io.jpress.oauth2.Oauth2Controller;
import io.jpress.oauth2.OauthUser;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.HashUtils;

import java.math.BigInteger;
import java.util.Date;

@UrlMapping(url = "/oauth")
public class OauthController extends Oauth2Controller {

	@Override
	public void onCallBack(OauthUser ouser) {
		User user = User.DAO.findFirstFromMetadata(ouser.getSource() + "_open_id", ouser.getOpenId());
		if (null == user) { // first login
			user = new User();
			user.setAvatar(ouser.getAvatar());
			user.setNickname(ouser.getNickname());
			user.setCreateSource(ouser.getSource());
			user.setCreated(new Date());
			user.setGender(ouser.getGender());
			user.setSalt(HashUtils.salt());

			user.save();
			MessageKit.sendMessage(Actions.USER_CREATED, user);

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
	public void onError(String errorMessage) {
		doAuthorizeFailure();
	}

	private void doAuthorizeFailure() {
		redirect(Consts.LOGIN_BASE_URL);
	}

	private void doAuthorizeSuccess(User user) {
		CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
		MessageKit.sendMessage(Actions.USER_LOGINED, user);
		redirect(Consts.USER_CENTER_BASE_URL);
	}

}
