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

import io.jpress.message.Actions;
import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;

@Listener(action = { Actions.CONTENT_ADD, Actions.CONTENT_UPDATE, Actions.CONTENT_DELETE })
public class ContentListener implements MessageListener {


	@Override
	public void onMessage(Message message) {

		// 文章添加到数据库
		if (Actions.CONTENT_ADD.equals(message.getAction())) {
			
		}

		// 文章被更新
		else if (Actions.CONTENT_UPDATE.equals(message.getAction())) {
			
		}
		
		// 文章被删除
		else if (Actions.CONTENT_DELETE.equals(message.getAction())) {
			
		}
	}



}
