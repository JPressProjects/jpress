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
import java.util.HashMap;
import java.util.Map;

public class Hooks {

	private Map<String, Method> hookMethods = new HashMap<String, Method>();
	private Map<String, Object> hookObjects = new HashMap<String, Object>();

	public void register(String hookName, Class<? extends Hook> clazz) {
		Method method = null;
		if (hookName.equals(HOOK_PROCESS_CONTROLLER)) {
			try {
				method = clazz.getDeclaredMethod("process_controller", HookController.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(!hookObjects.containsKey(hookName)){
			try {
				hookObjects.put(hookName, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (null != method) {
			hookMethods.put(hookName, method);
		} else {

		}
	}

	public Method method(String hookName) {
		return hookMethods.get(hookName);
	}
	
	public Object object(String hookName) {
		return hookObjects.get(hookName);
	}

	public static final String HOOK_TARGET_CONVERTE = "target_converte";
	public static final String HOOK_PROCESS_CONTROLLER = "process_controller";

}
