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
package io.jpress.core.addon;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.log.Log;

public class Hooks {
	private static final Log log = Log.getLog(Hooks.class);
	private Map<String, Method> hookMethods = new ConcurrentHashMap<String, Method>();
	private Map<String, Hook> hookObjects = new ConcurrentHashMap<String, Hook>();

	public void register(String hook, Class<? extends Hook> clazz) {
		Method method = null;
		Method[] methods = clazz.getMethods();
		if (methods != null && methods.length > 0) {
			for (Method m : methods) {
				if (hook.equals(m.getName())) {
					method = m;
					break;
				}
			}
		}

		if (null != method) {
			if (!hookObjects.containsKey(hook)) {
				try {
					hookObjects.put(hook, clazz.newInstance());
				} catch (Exception e) {
					log.error("Hooks register error", e);
				}
			}
			hookMethods.put(hook, method);
		} else {

		}
	}

	public Method method(String hook) {
		return hookMethods.get(hook);
	}

	public Hook hook(String hook) {
		return hookObjects.get(hook);
	}

}
