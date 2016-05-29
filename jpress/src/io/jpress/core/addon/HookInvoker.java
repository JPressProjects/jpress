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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import com.jfinal.render.Render;

public class HookInvoker {
	private static final Log log = Log.getLog(HookInvoker.class);

	public static String router_converte(String target, HttpServletRequest request, HttpServletResponse response) {
		String newTarget = (String) invoke("target_converte", request, response);
		return newTarget == null ? target : newTarget;
	}

	public static Render process_controller(AddonController controller) {
		return (Render) invoke("process_controller", controller);
	}

	public static Boolean intercept(Invocation inv) {
		return (Boolean) invoke("intercept", inv);
	}

	private static Object invoke(String hookName, Object... objects) {
		List<Addon> addons = AddonManager.get().getStartedAddons();
		for (Addon addon : addons) {
			if (addon.getHasError()) {
				continue;
			}
			Method method = addon.getHooks().method(hookName);
			if (method != null) {
				Hook hook = null;
				try {
					hook = addon.getHooks().hook(hookName);
					Object ret = method.invoke(hook, objects);
					if (!hook.letNextHookInvoke()) {
						return ret;
					}
				} catch (Throwable e) {
					addon.setHasError(true);
					log.error("HookInvoker invoke error", e);
				} finally {
					if (hook != null) {
						hook.hookInvokeFinished();
					}
				}
			}
		}
		return null;
	}

}
