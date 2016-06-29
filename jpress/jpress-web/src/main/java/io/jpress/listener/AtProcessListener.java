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
package io.jpress.listener;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class AtProcessListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		// 新增文章
		if (Actions.CONTENT_ADD.equals(message.getAction())) {

		}

		// 新增评论
		else if (Actions.COMMENT_ADD.equals(message.getAction())) {

		}

	}

	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.CONTENT_ADD);
		messageAction.register(Actions.COMMENT_ADD);
	}

	
	static Pattern userPattern = Pattern.compile("@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");
	public static String _GenerateRefererLinks(String msg, List<BigInteger> userIds) {
		StringBuilder html = new StringBuilder();
		int lastIdx = 0;
		Matcher matchr = userPattern.matcher(msg);
		while (matchr.find()) {
			String groupString = matchr.group();
			String username = groupString.substring(1, groupString.length()).trim();
			html.append(msg.substring(lastIdx, matchr.start()));
			User user = UserQuery.findUserByUsername(username);
			if (user != null && !user.isFrozen()) {
				html.append("<a href='/user/" + user.getId() + "' class='referer' target='_blank'>@");
				html.append(username.trim());
				html.append("</a> ");
				if (userIds != null && !userIds.contains(user.getId())) {
					userIds.add(user.getId());
				}
			} else {
				html.append(groupString);
			}
			lastIdx = matchr.end();
		}
		html.append(msg.substring(lastIdx));
		return html.toString();
	}

}
