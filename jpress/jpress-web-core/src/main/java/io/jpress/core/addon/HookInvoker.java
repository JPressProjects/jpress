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
import com.jfinal.core.Controller;
import com.jfinal.render.Render;

import io.jpress.menu.MenuManager;

public class HookInvoker {

	public static String routerConverte(String target, HttpServletRequest request, HttpServletResponse response) {
		return (String) AddonManager.me().invokeHook(Hooks.ROUTER_CONVERTE, request, response);
	}

	public static Render processController(Controller controller) {
		return (Render) AddonManager.me().invokeHook(Hooks.PROCESS_CONTROLLER, controller);
	}

	public static Boolean intercept(Invocation inv) {
		return (Boolean) AddonManager.me().invokeHook(Hooks.INTERCEPT, inv);
	}

	public static Render indexRenderBefore(Controller controller) {
		return (Render) AddonManager.me().invokeHook(Hooks.INDEX_RENDER_BEFORE, controller);
	}

	public static void indexRenderAfter(Controller controller) {
		AddonManager.me().invokeHook(Hooks.INDEX_RENDER_AFTER, controller);
	}

	public static Render taxonomyRenderBefore(Controller controller) {
		return (Render) AddonManager.me().invokeHook(Hooks.TAXONOMY_RENDER_BEFORE, controller);
	}

	public static void taxonomyRenderAfter(Controller controller) {
		AddonManager.me().invokeHook(Hooks.TAXONOMY_RENDER_AFTER, controller);
	}

	public static Render contentRenderBefore(Controller controller) {
		return (Render) AddonManager.me().invokeHook(Hooks.CONTENT_RENDER_BEFORE, controller);
	}

	public static void contentRenderAfter(Controller controller) {
		AddonManager.me().invokeHook(Hooks.CONTENT_RENDER_AFTER, controller);
	}

	public static void menuInitBefore(MenuManager menuManager) {
		AddonManager.me().invokeHook(Hooks.MENU_INIT_BEFORE, menuManager);
	}

	public static void menuInitAfter(MenuManager menuManager) {
		AddonManager.me().invokeHook(Hooks.MENU_INIT_AFTER, menuManager);
	}

}
