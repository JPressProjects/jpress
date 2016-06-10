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
package io.jpress.plugin.message.listener;

import java.util.Date;

import io.jpress.model.User;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class UserActionListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		if (message.getAction().equals(Actions.USER_LOGINED)) {
			User user = message.getData();
			user.setLogged(new Date());
			user.update();
		}
		
		else if (message.getAction().equals(Actions.USER_CREATED)) {
			User user = message.getData();
			user.setLogged(new Date());
			user.update();
		}
	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.USER_LOGINED);
		messageAction.register(Actions.USER_CREATED);
	}

}
