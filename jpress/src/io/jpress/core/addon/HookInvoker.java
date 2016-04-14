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

import com.jfinal.render.Render;

public class HookInvoker {

	public static String target_converte(String target, HttpServletRequest request, HttpServletResponse response) {

		List<Addon> addons = AddonKit.getManager().getStartedAddons();
		for (Addon addon : addons) {
			Method method = addon.getHooks().method("target_converte");
			if (method != null)
				try {
					return (String) method.invoke(addon.getHooks().object("target_converte"), target, request,
							response);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		return target;
	}

	public static Render process_controller(HookController controller) {

		List<Addon> addons = AddonKit.getManager().getStartedAddons();
		for (Addon addon : addons) {
			Method method = addon.getHooks().method("process_controller");
			if (method != null)
				try {
					return (Render) method.invoke(addon.getHooks().object("process_controller"), controller);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return null;
	}

}
