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
package io.jpress.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfinal.core.JFinal;
import com.jfinal.log.Log;

import io.jpress.message.annotation.Listener;

public class MessageManager {

	private final ExecutorService threadPool;
	private final Map<String, List<MessageListener>> asyncListenerMap;
	private final Map<String, List<MessageListener>> listenerMap;
	private static final Log log = Log.getLog(MessageManager.class);

	private static MessageManager manager = new MessageManager();

	private MessageManager() {
		threadPool = Executors.newFixedThreadPool(5);
		asyncListenerMap = new ConcurrentHashMap<String, List<MessageListener>>();
		listenerMap = new ConcurrentHashMap<String, List<MessageListener>>();
	}

	public static MessageManager me() {
		return manager;
	}

	public void unRegisterListener(Class<? extends MessageListener> listenerClass) {

		deleteListner(listenerMap, listenerClass);
		deleteListner(asyncListenerMap, listenerClass);
		
		if (JFinal.me().getConstants().getDevMode()) {
			System.out.println(String.format("listener[%s]-->>unRegisterListener.", listenerClass));
		}

	}

	private void deleteListner(Map<String, List<MessageListener>> map, Class<? extends MessageListener> listenerClass) {
		for (Map.Entry<String, List<MessageListener>> entry : map.entrySet()) {
			MessageListener deleteListener = null;
			for (MessageListener listener : entry.getValue()) {
				if (listener.getClass() == listenerClass) {
					deleteListener = listener;
				}
			}
			if (deleteListener != null) {
				entry.getValue().remove(deleteListener);
			}
		}
	}

	public void registerListener(Class<? extends MessageListener> listenerClass) {

		if (listenerClass == null) {
			return;
		}

		Listener listenerAnnotation = listenerClass.getAnnotation(Listener.class);
		if (listenerAnnotation == null) {
			log.warn("listenerClass[" + listenerAnnotation + "] resigter fail,because not use Listener annotation.");
			return;
		}

		String[] actions = listenerAnnotation.action();
		if (actions == null || actions.length == 0) {
			log.warn("listenerClass[" + listenerAnnotation + "] resigter fail,because action is null or blank.");
			return;
		}

		if (listenerHasRegisterBefore(listenerClass)) {
			return;
		}

		MessageListener listener = newListener(listenerClass);
		if (listener == null) {
			return;
		}

		for (String action : actions) {
			List<MessageListener> list = null;
			if (listenerAnnotation.async()) {
				list = asyncListenerMap.get(action);
			} else {
				list = listenerMap.get(action);
			}
			if (null == list) {
				list = new ArrayList<MessageListener>();
			}
			if (list.isEmpty() || !list.contains(listener)) {
				list.add(listener);
			}
			Collections.sort(list, new Comparator<MessageListener>() {
				@Override
				public int compare(MessageListener o1, MessageListener o2) {
					Listener l1 = o1.getClass().getAnnotation(Listener.class);
					Listener l2 = o2.getClass().getAnnotation(Listener.class);
					return l1.weight() - l2.weight();
				}
			});

			if (listenerAnnotation.async()) {
				asyncListenerMap.put(action, list);
			} else {
				listenerMap.put(action, list);
			}
		}
		
		if (JFinal.me().getConstants().getDevMode()) {
			System.out.println(String.format("listener[%s]-->>registered.", listener));
		}

	}

	private MessageListener newListener(Class<? extends MessageListener> listenerClass) {
		MessageListener listener = null;
		try {
			listener = listenerClass.newInstance();
		} catch (Throwable e) {
			log.error(String.format("listener \"%s\" newInstance is error. ", listenerClass), e);
		}
		return listener;
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

		for (Map.Entry<String, List<MessageListener>> entry : asyncListenerMap.entrySet()) {
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
		String action = message.getAction();

		List<MessageListener> syncListeners = listenerMap.get(action);
		if (syncListeners != null && !syncListeners.isEmpty()) {
			invokeListeners(message, syncListeners);
		}

		List<MessageListener> listeners = asyncListenerMap.get(action);
		if (listeners != null && !listeners.isEmpty()) {
			invokeListenersAsync(message, listeners);
		}

	}

	private void invokeListeners(final Message message, List<MessageListener> syncListeners) {
		for (final MessageListener listener : syncListeners) {
			try {
				if (JFinal.me().getConstants().getDevMode()) {
					System.out.println(String.format("listener[%s]-->>onMessage(%s)", listener, message));
				}
				listener.onMessage(message);
			} catch (Throwable e) {
				log.error(String.format("listener[%s] onMessage is erro! ", listener.getClass()), e);
			}
		}
	}

	private void invokeListenersAsync(final Message message, List<MessageListener> listeners) {
		for (final MessageListener listener : listeners) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (JFinal.me().getConstants().getDevMode()) {
							System.out.println(String.format("listener[%s]-->>onMessage(%s) in async", listener, message));
						}
						listener.onMessage(message);
					} catch (Throwable e) {
						log.error(String.format("listener[%s] onMessage is erro! ", listener.getClass()), e);
					}
				}
			});
		}
	}

}
