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

import io.jpress.core.ClassScaner;

public class MessagePlugin implements IPlugin {

	private final ExecutorService threadPool;
	private final Map<String, List<MessageListener>> listenerMap;
	private final Map<String, List<MessageListener>> syncListenerMap;
	private static final Log log = Log.getLog(MessagePlugin.class);

	public MessagePlugin() {
		threadPool = Executors.newFixedThreadPool(5);
		listenerMap = new ConcurrentHashMap<String, List<MessageListener>>();
		syncListenerMap = new ConcurrentHashMap<String, List<MessageListener>>();
	}

	public void registerListener(Class<? extends MessageListener> listenerClass) {

		if (listenerClass == null) {
			return;
		}

		if (listenerHasRegisterBefore(listenerClass)) {
			return;
		}

		MessageListener listener = null;
		try {
			listener = listenerClass.newInstance();
		} catch (Throwable e) {
			log.error(String.format("listener \"%s\" newInstance is error. ", listenerClass), e);
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

		for (String action : actions.getSyncActions()) {
			List<MessageListener> list = syncListenerMap.get(action);
			if (null == list) {
				list = new ArrayList<MessageListener>();
			}
			if (!list.contains(listener)) {
				list.add(listener);
			}
			syncListenerMap.put(action, list);
		}
	}

	private boolean listenerHasRegisterBefore(Class<? extends MessageListener> listenerClass) {

		for (Map.Entry<String, List<MessageListener>> entry : listenerMap.entrySet()) {
			List<MessageListener> listeners = entry.getValue();
			if (listeners == null || listeners.isEmpty()) {
				continue;
			}
			for (MessageListener ml : listeners) {
				if (listenerClass == ml.getClass()) {
					return true;
				}
			}
		}

		for (Map.Entry<String, List<MessageListener>> entry : syncListenerMap.entrySet()) {
			List<MessageListener> listeners = entry.getValue();
			if (listeners == null || listeners.isEmpty()) {
				continue;
			}
			for (MessageListener ml : listeners) {
				if (listenerClass == ml.getClass()) {
					return true;
				}
			}
		}

		return false;
	}

	public void pulish(final Message message) {
		String key = message.getAction();

		List<MessageListener> syncListeners = syncListenerMap.get(key);
		if (syncListeners != null && !syncListeners.isEmpty()) {
			invokeListenersInSync(message, syncListeners);
		}

		List<MessageListener> listeners = listenerMap.get(key);
		if (listeners != null && !listeners.isEmpty()) {
			invokeListeners(message, listeners);
		}

	}

	private void invokeListenersInSync(final Message message, List<MessageListener> syncListeners) {
		for (final MessageListener listener : syncListeners) {
			try {
				listener.onMessage(message);
			} catch (Throwable e) {
				log.error(String.format("listener[%s] onMessage is erro! ", listener.getClass()), e);
			}
		}
	}

	private void invokeListeners(final Message message, List<MessageListener> listeners) {
		for (final MessageListener listener : listeners) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						listener.onMessage(message);
					} catch (Throwable e) {
						log.error(String.format("listener[%s] onMessage is erro! ", listener.getClass()), e);
					}
				}
			});
		}
	}

	@Override
	public boolean start() {
		MessageKit.init(this);
		autoRegister();
		return true;
	}

	private void autoRegister() {
		List<Class<MessageListener>> list = ClassScaner.scanSubClass(MessageListener.class, true);
		if (list != null && list.size() > 0) {
			for (Class<MessageListener> clazz : list) {
				registerListener(clazz);
			}
		}
	}

	@Override
	public boolean stop() {
		return true;
	}

}
