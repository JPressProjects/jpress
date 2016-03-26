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
package io.jpress.plugin.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;

public class MessagePlugin implements IPlugin {

	private final ExecutorService threadPool;
	private final Map<String, List<MessageListener>> listenerMap;
	private static final Log log = Log.getLog(MessagePlugin.class);

	public MessagePlugin() {
		threadPool = Executors.newFixedThreadPool(5);
		listenerMap = new ConcurrentHashMap<String, List<MessageListener>>();
	}

	public void registerListener(Class<? extends MessageListener> listenerClass) {

		MessageListener listener = null;
		try {
			listener = listenerClass.newInstance();
		} catch (Exception e) {
			log.error(String.format("listener \"%s\" newInstance is error. ", listenerClass) , e);
			return;
		}

		MessageAction actions = new MessageAction();
		listener.onRegisterAction(actions);
		
		for (String action : actions.getActions()) {

			List<MessageListener> list = listenerMap.get(action);
			if (null == list) {
				list = new ArrayList<MessageListener>();
			}

			if (!list.contains(listener)) {
				list.add(listener);
			}
			listenerMap.put(action, list);
		}

	}

	public void pulish(final Message message) {
		String key = message.getClass().getName();
		List<MessageListener> listeners = listenerMap.get(key);

		if (null == listeners || listeners.size() == 0) {
			log.warn("there is no listeners for message : " + message);
			return;
		}

		for (final MessageListener listener : listeners) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						listener.onMessage(message);
					} catch (Exception e) {
						log.error(String.format(
								"listener[%s] onMessage is erro! ",
								listener.getClass()), e);
					}
				}
			});
		}
	}

	@Override
	public boolean start() {
		MessageKit.init(this);
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
