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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Invocation;
import com.jfinal.render.Render;

public class HookInvoker {

	public static String router_converte(String target, HttpServletRequest request, HttpServletResponse response) {
		String newTarget = (String) AddonManager.get().invokeHook("target_converte", request, response);
		return newTarget == null ? target : newTarget;
	}

	public static Render process_controller(AddonController controller) {
		return (Render) AddonManager.get().invokeHook("process_controller", controller);
	}

	public static Boolean intercept(Invocation inv) {
		return (Boolean) AddonManager.get().invokeHook("intercept", inv);
	}

}
