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
package io.jpress.message.plugin;

import java.util.List;

import com.jfinal.plugin.IPlugin;

import io.jpress.message.MessageListener;
import io.jpress.message.MessageManager;
import io.jpress.utils.ClassUtils;

public class MessagePlugin implements IPlugin {


	@Override
	public boolean start() {
		autoRegister();
		return true;
	}

	private void autoRegister() {
		List<Class<MessageListener>> list = ClassUtils.scanSubClass(MessageListener.class, true);
		if (list != null && list.size() > 0) {
			for (Class<MessageListener> clazz : list) {
				MessageManager.me().registerListener(clazz);
			}
		}
	}

	@Override
	public boolean stop() {
		return true;
	}

}
